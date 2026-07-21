package vinix.services;

import java.util.List;

import vinix.dto.response.TransactionResponseDTO;

public interface TransactionService {
    TransactionResponseDTO findById(Long id);
    List<TransactionResponseDTO> findByAccountId(Long accountId);
}