package vinix.services;

import java.util.List;

import vinix.dto.request.AccountRequestDTO;
import vinix.dto.request.DepositRequestDTO;
import vinix.dto.request.TransferRequestDTO;
import vinix.dto.request.WithdrawRequestDTO;
import vinix.dto.response.AccountResponseDTO;
import vinix.dto.response.TransactionResponseDTO;

public interface AccountService {
    AccountResponseDTO create(AccountRequestDTO dto);
    AccountResponseDTO findById(Long id);
    List<AccountResponseDTO> findAll();
    TransactionResponseDTO deposit(Long accountId, DepositRequestDTO dto);
    TransactionResponseDTO withdraw(Long accountId, WithdrawRequestDTO dto);
    List<TransactionResponseDTO> transfer(TransferRequestDTO dto);
}
