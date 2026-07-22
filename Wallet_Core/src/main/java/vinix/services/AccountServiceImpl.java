package vinix.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vinix.dto.request.AccountRequestDTO;
import vinix.dto.request.DepositRequestDTO;
import vinix.dto.request.TransferRequestDTO;
import vinix.dto.request.WithdrawRequestDTO;
import vinix.dto.response.AccountResponseDTO;
import vinix.dto.response.TransactionResponseDTO;
import vinix.entities.Account;
import vinix.entities.Transaction;
import vinix.entities.TransactionType;
import vinix.mapper.AccountMapper;
import vinix.mapper.TransactionMapper;
import vinix.repositories.AccountRepository;
import vinix.repositories.TransactionRepository;
import vinix.resources.exceptions.DuplicateDocumentException;
import vinix.services.exceptions.AccountNotFoundException;
import vinix.services.exceptions.InsufficientBalanceException;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;

 // No AccountServiceImpl.create()
    @Override
    @Transactional
    public AccountResponseDTO create(AccountRequestDTO dto) {
        String cleanDocument = dto.document().replaceAll("[^0-9]", "");

        if (accountRepository.findByDocument(cleanDocument).isPresent()) {
            throw new DuplicateDocumentException("Já existe uma conta cadastrada com o documento " + cleanDocument);
        }

        Account account = accountMapper.toEntity(dto);
        return accountMapper.toResponseDTO(accountRepository.save(account));
    }

    @Override
    public AccountResponseDTO findById(Long id) {
        return accountMapper.toResponseDTO(findAccountOrThrow(id));
    }

    @Override
    public List<AccountResponseDTO> findAll() {
        return accountRepository.findAll().stream()
                .map(accountMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public TransactionResponseDTO deposit(Long accountId, DepositRequestDTO dto) {
        Account account = findAccountOrThrow(accountId);
        account.setBalance(account.getBalance().add(dto.amount()));
        //dirty checking faz UPDATE

        Transaction transaction = registerTransaction(
                account, TransactionType.DEPOSIT, dto.amount(), "Depósito em conta");

        return transactionMapper.toResponseDTO(transaction);
    }

    @Override
    @Transactional
    public TransactionResponseDTO withdraw(Long accountId, WithdrawRequestDTO dto) {
        Account account = findAccountOrThrow(accountId);

        if (account.getBalance().compareTo(dto.amount()) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente para a conta " + accountId);
        }

        account.setBalance(account.getBalance().subtract(dto.amount()));

        Transaction transaction = registerTransaction(
                account, TransactionType.WITHDRAW, dto.amount(), "Saque em conta");

        return transactionMapper.toResponseDTO(transaction);
    }

    @Override
    @Transactional
    public List<TransactionResponseDTO> transfer(TransferRequestDTO dto) {
        Account source = findAccountOrThrow(dto.sourceAccountId());
        Account target = findAccountOrThrow(dto.targetAccountId());

        if (source.getBalance().compareTo(dto.amount()) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente na conta de origem: " + source.getId());
        }

        source.setBalance(source.getBalance().subtract(dto.amount()));
        target.setBalance(target.getBalance().add(dto.amount()));

        Transaction out = registerTransaction(source, TransactionType.TRANSFER_OUT, dto.amount(),
                "Transferência para conta " + target.getId());
        Transaction in = registerTransaction(target, TransactionType.TRANSFER_IN, dto.amount(),
                "Transferência recebida da conta " + source.getId());

        return transactionMapper.toResponseDTOList(List.of(out, in));
    }

    private Transaction registerTransaction(Account account, TransactionType type, BigDecimal amount, String description) {
        Transaction transaction = Transaction.builder()
                .account(account)
                .type(type)
                .amount(amount)
                .balanceAfter(account.getBalance())
                .description(description)
                .build();
        return transactionRepository.save(transaction);
    }

    private Account findAccountOrThrow(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada: " + id));
    }
}
