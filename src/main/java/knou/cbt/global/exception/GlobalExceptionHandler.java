package knou.cbt.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 그 외 모든 예외
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        log.error("Unhandled exception", ex);
        model.addAttribute("message", "알 수 없는 오류가 발생했습니다.");
        return "error/500"; // /templates/error/500.html
    }
}
