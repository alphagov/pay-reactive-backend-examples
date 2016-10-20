package firebreak.react.rat;

public class AppConfig {

    private String authDelay;

    public String getAuthDelay() {
        return authDelay;
    }

    public Integer authDelayAsInt(){
        return Integer.parseInt(authDelay);
    }

    public void setAuthDelay(String authDelay) {
        this.authDelay = authDelay;
    }
}
