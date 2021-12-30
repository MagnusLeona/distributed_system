package magnus.distributed.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MagnusResponse<T> {
    public int code;
    public String message;
    public T data;

    public MagnusResponse(int code, String msg) {
        this(code, msg, null);
    }
}
