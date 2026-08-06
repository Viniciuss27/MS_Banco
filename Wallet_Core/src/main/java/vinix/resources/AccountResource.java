package vinix.resources;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import vinix.dto.request.AccountRequestDTO;
import vinix.dto.request.DepositRequestDTO;
import vinix.dto.request.TransferRequestDTO;
import vinix.dto.request.WithdrawRequestDTO;
import vinix.dto.response.AccountResponseDTO;
import vinix.dto.response.TransactionResponseDTO;
import vinix.services.AccountService;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/accounts")
public class AccountResource {

    private final AccountService service;

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> findAll() {
        List<AccountResponseDTO> list = service.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> findById(
            @PathVariable @Positive Long id) {

        AccountResponseDTO response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AccountResponseDTO> create(
            @RequestBody @Valid AccountRequestDTO dto) {

        AccountResponseDTO response = service.create(dto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<TransactionResponseDTO> deposit(
            @PathVariable @Positive Long accountId,
            @RequestBody @Valid DepositRequestDTO dto) {

        TransactionResponseDTO response = service.deposit(accountId, dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<TransactionResponseDTO> withdraw(
            @PathVariable @Positive Long accountId,
            @RequestBody @Valid WithdrawRequestDTO dto) {

        TransactionResponseDTO response = service.withdraw(accountId, dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<List<TransactionResponseDTO>> transfer(
            @RequestBody @Valid TransferRequestDTO dto) {

        List<TransactionResponseDTO> response = service.transfer(dto);
        return ResponseEntity.ok(response);
    }
}