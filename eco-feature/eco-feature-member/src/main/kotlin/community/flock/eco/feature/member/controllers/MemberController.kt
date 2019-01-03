package community.flock.eco.feature.member.controllers

import community.flock.eco.core.events.Event
import community.flock.eco.feature.member.model.Member
import community.flock.eco.feature.member.model.MemberGender
import community.flock.eco.feature.member.model.MemberGroup
import community.flock.eco.feature.member.model.MemberStatus
import community.flock.eco.feature.member.repositories.MemberGroupRepository
import community.flock.eco.feature.member.repositories.MemberRepository
import community.flock.eco.feature.member.specifications.MemberSpecification
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*


sealed class MemberEvent(open val member: Member) : Event
data class CreateMemberEvent(override val member: Member) : MemberEvent(member)
data class UpdateMemberEvent(override val member: Member) : MemberEvent(member)
data class DeleteMemberEvent(override val member: Member) : MemberEvent(member)

@RestController
@RequestMapping("/api/members")
class MemberController(
        private val memberRepository: MemberRepository,
        private val memberGroupRepository: MemberGroupRepository,
        private val publisher: ApplicationEventPublisher
) {


    data class MemberForm(

            val firstName: String,
            val infix: String? = null,
            val surName: String,

            val email: String? = null,

            val phoneNumber: String? = null,

            val street: String? = null,
            val houseNumber: String? = null,
            val houseNumberExtension: String? = null,
            val postalCode: String? = null,
            val city: String? = null,
            val country: String? = null,

            val gender: MemberGender? = null,
            val birthDate: LocalDate? = null,

            val groups: Set<MemberGroup> = setOf(),
            val fields: Map<String, String> = mapOf(),

            val status: MemberStatus = MemberStatus.NEW
    )

    data class MergeForm(val mergeMemberIds: List<Long>, val newMember: MemberForm)

    @GetMapping
    @PreAuthorize("hasAuthority('MemberAuthority.READ')")
    fun findAll(
            @RequestParam search: String?,
            @RequestParam statuses: Set<MemberStatus>?,
            @RequestParam groups: Set<String>?,
            page: Pageable): ResponseEntity<List<Member>> {

        val specification = MemberSpecification(
                search = search ?: "",
                statuses = statuses ?: setOf(
                        MemberStatus.NEW,
                        MemberStatus.ACTIVE,
                        MemberStatus.DISABLED),
                groups = groups?.let { memberGroupRepository.findByCodes(it).toSet() } ?: setOf()
        )
        val res = memberRepository.findAll(specification, page)
        val headers = HttpHeaders()
        headers.set("x-page", page.pageNumber.toString())
        headers.set("x-total", res.totalElements.toString())
        return ResponseEntity(res.content.toList(), headers, HttpStatus.OK)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MemberAuthority.READ')")
    fun findById(
            @PathVariable("id") id: String): Optional<Member> = memberRepository
            .findById(id.toLong())

    @PostMapping
    @PreAuthorize("hasAuthority('MemberAuthority.WRITE')")
    fun create(@RequestBody form: MemberForm): Member {
        return form.toMember()
                .let { memberRepository.save(it) }
                .apply { publisher.publishEvent(CreateMemberEvent(this)) }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MemberAuthority.WRITE')")
    fun update(@PathVariable("id") id: String, @RequestBody member: Member): Member = member
            .copy(
                    id = id.toLong(),
                    groups = memberGroupRepository
                            .findAllById(member.groups.map { it.id })
                            .toSet())
            .let { memberRepository.save(it) }
            .apply { publisher.publishEvent(UpdateMemberEvent(this)) }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MemberAuthority.WRITE')")
    fun delete(@PathVariable("id") id: String) = memberRepository.findById(id.toLong())
            .ifPresent { member ->
                member
                        .copy(status = MemberStatus.DELETED)
                        .let { memberRepository.save(it) }
                        .apply { publisher.publishEvent(DeleteMemberEvent(this)) }
            }

    @PostMapping("/merge")
    @PreAuthorize("hasAuthority('MemberAuthority.WRITE')")
    fun merge(@RequestBody form: MergeForm): Member {
        form.mergeMemberIds.map { merge(it) }
        return form.newMember.toMember().let { memberRepository.save(it) }
    }

    private fun merge(id: Long) = memberRepository
            .findById(id)
            .ifPresent { member ->
                member.copy(status = MemberStatus.MERGED).let { memberRepository.save(it) }
            }

    private fun MemberForm.toMember(): Member {
        return Member(
                firstName = this.firstName,
                infix = this.infix,
                surName = this.surName,
                email = this.email,
                phoneNumber = this.phoneNumber,
                street = this.street,
                houseNumber = this.houseNumber,
                houseNumberExtension = this.houseNumberExtension,
                postalCode = this.postalCode,
                city = this.postalCode,
                country = this.country,
                gender = this.gender,
                groups = this.groups,
                fields = this.fields,
                birthDate = this.birthDate,
                status = this.status
        )
    }

}
