package org.example.dtos.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.dtos.AbstractDto;

public class UsersFilterDto extends AbstractDto
{
    @JsonProperty("username")
    private String username;

    @JsonProperty("start")
    private Integer start;

    @JsonProperty("offset")
    private Integer offset;

    public UsersFilterDto()
    {
    }

    public UsersFilterDto(String username, Integer start, Integer offset)
    {
        this.username = username;
        this.start = start;
        this.offset = offset;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public Integer getStart()
    {
        return start;
    }

    public void setStart(Integer start)
    {
        this.start = start;
    }

    public Integer getOffset()
    {
        return offset;
    }

    public void setOffset(Integer offset)
    {
        this.offset = offset;
    }
}
