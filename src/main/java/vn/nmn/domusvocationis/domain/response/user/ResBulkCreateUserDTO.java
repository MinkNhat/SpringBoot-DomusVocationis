package vn.nmn.domusvocationis.domain.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ResBulkCreateUserDTO {
    private int successCount;
    private int errorCount;
    private List<ErrorItem> ErrorDetails;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorItem {
        private int index;
        private String errMessage;
    }
}
