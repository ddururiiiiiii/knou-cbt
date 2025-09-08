package knou.cbt.web.notice;

import jakarta.validation.Valid;
import knou.cbt.common.api.PageRequest;
import knou.cbt.common.api.PageResponse;
import knou.cbt.domain.notice.dto.NoticeRequest;
import knou.cbt.domain.notice.dto.NoticeResponse;
import knou.cbt.domain.notice.exception.NoticeNotFoundException;
import knou.cbt.domain.notice.model.Notice;
import knou.cbt.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeViewController {

    private final NoticeService noticeService;

    /**
     * 공지사항 목록 (비회원도 접근 가능)
     * @param pageRequest
     * @param keyword
     * @param useYn
     * @param model
     * @return
     */
    @GetMapping
    public String list(@Valid PageRequest pageRequest,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String useYn,
                       Model model) {

        PageResponse<NoticeResponse> pageResponse =
                noticeService.listPage(keyword, useYn, pageRequest);

        model.addAttribute("pagination", pageResponse);
        model.addAttribute("keyword", keyword);
        model.addAttribute("useYn", useYn);

        return "notice/noticeList";
    }

    /**
     * 공지사항 상세보기 (비회원도 접근 가능)
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Notice notice = noticeService.get(id);
        model.addAttribute("notice", NoticeResponse.of(notice));
        return "notice/noticeDetail";
    }

    /**
     * 공지사항 등록 화면 (관리자만 접근 가능)
     * @param model
     * @return
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("formTitle", "공지사항 등록");
        model.addAttribute("actionUrl", "/notices");
        model.addAttribute("method", "post");
        model.addAttribute("notice", new NoticeRequest());
        return "notice/noticeForm";
    }

    /**
     * 공지사항 등록 처리 (관리자만)
     * @param req
     * @param bindingResult
     * @param model
     * @return
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@Valid @ModelAttribute("notice") NoticeRequest req,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "공지사항 등록");
            model.addAttribute("actionUrl", "/notices");
            model.addAttribute("method", "post");
            return "notice/noticeForm";
        }
        noticeService.create(req);
        return "redirect:/notices";
    }

    /**
     * 공지사항 수정 화면 (관리자만)
     * @param id
     * @param model
     * @return
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        Notice existing = noticeService.get(id);
        if (existing == null) {
            throw new NoticeNotFoundException(id);
        }
        NoticeRequest req = new NoticeRequest();
        req.setTitle(existing.getTitle());
        req.setContent(existing.getContent());
        req.setPinned(existing.getPinned());
        req.setUseYn(existing.getUseYn());

        model.addAttribute("formTitle", "공지사항 수정");
        model.addAttribute("actionUrl", "/notices/" + id);
        model.addAttribute("method", "patch");
        model.addAttribute("notice", req);
        return "notice/noticeForm";
    }

    /**
     * 공지사항 수정 처리 (관리자만)
     * @param id
     * @param req
     * @param bindingResult
     * @param model
     * @return
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("notice") NoticeRequest req,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "공지사항 수정");
            model.addAttribute("actionUrl", "/notices/" + id);
            model.addAttribute("method", "patch");
            return "notice/noticeForm";
        }
        noticeService.update(id, req);
        return "redirect:/notices/" + id;
    }

    /**
     * 공지사항 삭제 처리 (관리자만)
     * @param id
     * @return
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        noticeService.delete(id);
        return "redirect:/notices";
    }
}
