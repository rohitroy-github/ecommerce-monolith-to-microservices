package springdev.ecomv0.monolithicmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder 
public class ProductAvailiblityResponse {

    private Long productId;
    private Integer quantity;
    private boolean available;

}
