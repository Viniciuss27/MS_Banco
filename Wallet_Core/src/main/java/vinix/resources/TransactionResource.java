package vinix.resources;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import vinix.dto.response.TransactionResponseDTO;
import vinix.services.TransactionService;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(value = "/transactions")
public class TransactionResource {

    private final TransactionService service;

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponseDTO>> findByAccountId(@PathVariable @Positive Long accountId) {
        return ResponseEntity.ok(service.findByAccountId(accountId));
    }
}