package vinix.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vinix.dto.response.TransactionResponseDTO;
import vinix.entities.Transaction;
import vinix.mapper.TransactionMapper;
import vinix.repositories.TransactionRepository;
import vinix.services.exceptions.TransactionNotFoundException;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public TransactionResponseDTO findById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transação não encontrada: " + id));
        return transactionMapper.toResponseDTO(transaction);
    }

    @Override
    public List<TransactionResponseDTO> findByAccountId(Long accountId) {
        return transactionMapper.toResponseDTOList(
                transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId));
    }
}
