package org.example.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.example.constants.Constants.*;
@Entity
@Table(name = "messages")
public class Message extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    @JoinColumn(name = "author_id", nullable = false)
    @NotNull(message = AUTHOR_NULL_ERROR_MESSAGE)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    @JoinColumn(name = "recipient_id", nullable = false)
    @NotNull(message = RECIPIENT_NULL_ERROR_MESSAGE)
    private User recipient;

    @NotBlank(message = CONTENT_BLANK_ERROR_MESSAGE)
    @Length(max = 500, message = OVER_THE_MAX_LENGTH_CONTENT_ERROR_MESSAGE)
    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @NotNull
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    protected Message() {
    }

    public Message(UUID id) {
        super(id);
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
