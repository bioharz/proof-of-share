package models.dto;

import play.data.validation.Constraints;

public class ClaimSatoshiDto {

    @Constraints.Required()
    @Constraints.Min(value = 1, message = "Please claim at least 1 Satoshi")
    @Constraints.Max(value = 1000000, message = "You can only claim 10000000 per request")
    protected long satoshi = 0;

    public long getSatoshi() {
        return satoshi;
    }

    public void setSatoshi(long satoshi) {
        this.satoshi = satoshi;
    }
}
