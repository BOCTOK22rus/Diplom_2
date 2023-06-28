package data.user.data;

public class ChangeData {

    private String email;
    private String name;
    private String accessToken;

    public ChangeData(String email, String name, String accessToken) {
        this.email = email;
        this.name = name;
        this.accessToken = accessToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}