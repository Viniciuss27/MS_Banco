package vinix.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import vinix.dto.response.TransactionResponseDTO;
import vinix.dto.response.TransactionResponseSummaryDTO;
import vinix.entities.Transaction;

@Mapper(
    componentModel = "spring",
    uses = { AccountMapper.class },
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TransactionMapper {

    // Detalhado
	@Mapping(target = "account", qualifiedByName = "toSummaryDTO")
	TransactionResponseDTO toResponseDTO(Transaction entity);
    List<TransactionResponseDTO> toResponseDTOList(List<Transaction> entities);

    // Resumo
    TransactionResponseSummaryDTO toSummaryDTO(Transaction entity);
    List<TransactionResponseSummaryDTO> toSummaryDTOList(List<Transaction> entities);
}

