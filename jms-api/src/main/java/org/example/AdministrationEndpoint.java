package org.example;

import org.example.dtos.user.*;

import java.util.concurrent.CompletableFuture;

public interface AdministrationEndpoint extends Endpoint
{
    CompletableFuture<Void> signUp(SignUpDto signUpDto);
    CompletableFuture<UserDto> signIn(SignInDto signInDto);
    CompletableFuture<UsersWrapperDto> loadUsers(UsersFilterDto filter);
}
