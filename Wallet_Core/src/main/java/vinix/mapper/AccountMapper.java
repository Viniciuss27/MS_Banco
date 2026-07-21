package vinix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import vinix.dto.request.AccountRequestDTO;
import vinix.dto.response.AccountResponseDTO;
import vinix.dto.response.AccountResponseSummaryDTO;
import vinix.entities.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper {

	//Detalhado
    AccountResponseDTO toResponseDTO(Account entity);

    //Resumo
    @Named("toSummaryDTO")
    AccountResponseSummaryDTO toSummaryDTO(Account entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "balance", source = "initialBalance")
    @Mapping(target = "createdAt", ignore = true)
    Account toEntity(AccountRequestDTO dto);
}
