package vn.nmn.domusvocationis.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.nmn.domusvocationis.domain.Session;
import vn.nmn.domusvocationis.domain.response.schedule.ResSessionDTO;
import vn.nmn.domusvocationis.service.SessionService;
import vn.nmn.domusvocationis.util.annotation.ApiMessage;
import vn.nmn.domusvocationis.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/sessions/{id}")
    @ApiMessage("get a session")
    public ResponseEntity<ResSessionDTO> getSessionById(@PathVariable Long id) throws IdInvalidException {
        Session session = sessionService.getSessionById(id);
        if (session == null) {
            throw new IdInvalidException("Session có id = " + id + " không tồn tại");
        }

        return ResponseEntity.ok(sessionService.convertToResSessionDTO(session));
    }

    @PostMapping("/sessions")
    @ApiMessage("Regis a session")
    public ResponseEntity<ResSessionDTO> createSession(@Valid @RequestBody Session session) {
        return ResponseEntity.ok(this.sessionService.createSession(session));
    }

    @PutMapping("/sessions")
    @ApiMessage("Regis a session")
    public ResponseEntity<ResSessionDTO> registrationSession(@RequestBody Session session) throws IdInvalidException {
        Session s = sessionService.getSessionById(session.getId());
        if (s == null) {
            throw new IdInvalidException("Session có id = " + session.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(this.sessionService.registerSession(s));
    }
}
