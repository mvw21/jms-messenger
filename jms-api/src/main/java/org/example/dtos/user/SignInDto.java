package org.example.dtos.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.dtos.AbstractDto;

public class SignInDto extends AbstractDto
{
    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    public SignInDto(String email, String password)
    {
        this.email = email;
        this.password = password;
    }

    public SignInDto()
    {
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

}
