package bomba.com.mobiads.bamba.data;

/**
 * Created by WAKY on g3/16/2017.
 */
public class ApiResponse {
    Boolean success;
    String msg;
    Account[] calls;

    public Account[] getCalls() {
        return calls;
    }

    public void setCalls(Account[] calls) {
        this.calls = calls;
    }

    public ApiResponse(Boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
