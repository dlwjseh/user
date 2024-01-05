package my.practice.user.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestApi {

    @GetMapping("/test1/t1")
    public ResponseEntity<?> test1(HttpServletRequest request) {
        request.getHeaderNames().asIterator()
                .forEachRemaining(h -> log.info("Test1 Header: Name:{}, Value:{}", h, request.getHeader(h)));
        return ResponseEntity.ok("hello1");
    }

    @GetMapping("/test2/t2")
    public ResponseEntity<?> test2(HttpServletRequest request) {
        request.getHeaderNames().asIterator()
                .forEachRemaining(h -> log.info("Test2 Header: Name:{}, Value:{}", h, request.getHeader(h)));
        return ResponseEntity.ok("hello2");
    }

}
