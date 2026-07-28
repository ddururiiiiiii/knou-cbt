package knou.cbt.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import knou.cbt.common.exception.InvalidFileTypeException;
import knou.cbt.domain.department.exception.DepartmentDeleteNotAllowedException;
import knou.cbt.domain.department.exception.DepartmentNotFoundException;
import knou.cbt.domain.exam.exception.ExamHasQuestionsException;
import knou.cbt.domain.exam.exception.ExamNotFoundException;
import knou.cbt.domain.notice.exception.NoticeNotFoundException;
import knou.cbt.domain.subject.exception.SubjectDeleteNotAllowedException;
import knou.cbt.domain.subject.exception.SubjectNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 로그인하지 않았거나 권한이 없는 사용자의 접근
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(HttpServletRequest request, Model model, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal());

        if (!authenticated) {
            // 로그인 안 한 사용자 -> 로그인 화면으로
            return "redirect:/admin/login";
        }

        log.warn("Access denied for {} at {}", auth.getName(), request.getRequestURI());
        response.setStatus(HttpStatus.FORBIDDEN.value());
        model.addAttribute("message", "접근 권한이 없습니다.");
        return "error/403";
    }

    // 존재하지 않는 리소스 조회
    @ExceptionHandler({
            ExamNotFoundException.class,
            SubjectNotFoundException.class,
            DepartmentNotFoundException.class,
            NoticeNotFoundException.class
    })
    public String handleNotFound(RuntimeException ex, Model model, HttpServletResponse response) {
        log.warn("Resource not found: {}", ex.getMessage());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    // 참조 데이터가 있어 삭제할 수 없는 경우
    @ExceptionHandler({
            DepartmentDeleteNotAllowedException.class,
            SubjectDeleteNotAllowedException.class,
            ExamHasQuestionsException.class
    })
    public String handleConflict(RuntimeException ex, Model model, HttpServletResponse response) {
        log.warn("Delete conflict: {}", ex.getMessage());
        response.setStatus(HttpStatus.CONFLICT.value());
        model.addAttribute("message", ex.getMessage());
        return "error/409";
    }

    // 잘못된 파일 업로드 (타입/확장자 불일치 등)
    @ExceptionHandler(InvalidFileTypeException.class)
    public String handleInvalidFileType(InvalidFileTypeException ex, Model model, HttpServletResponse response) {
        log.warn("Invalid file upload: {}", ex.getMessage());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        model.addAttribute("message", ex.getMessage());
        return "error/400";
    }

    // 그 외 모든 예외
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model, HttpServletResponse response) {
        log.error("Unhandled exception", ex);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("message", "알 수 없는 오류가 발생했습니다.");
        return "error/500"; // /templates/error/500.html
    }
}
