package vinix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import vinix.dto.response.AccountResponseDTO;
import vinix.dto.response.AccountResponseSummaryDTO;
import vinix.entities.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper {

	//Detalhado
    AccountResponseDTO toResponseDTO(Account entity);

    @Named("toSummaryDTO")
    AccountResponseSummaryDTO toSummaryDTO(Account entity);
}
