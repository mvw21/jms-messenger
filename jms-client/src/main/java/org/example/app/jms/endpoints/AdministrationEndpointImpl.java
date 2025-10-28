package org.example.app.jms.endpoints;

import org.example.AdministrationEndpoint;
import org.example.app.jms.OperationInvoker;
import org.example.constants.OperationsNames;
import org.example.dtos.user.*;

import java.util.concurrent.CompletableFuture;

public class AdministrationEndpointImpl extends AbstractEndpointImpl implements AdministrationEndpoint
{
    public AdministrationEndpointImpl(OperationInvoker operationInvoker)
    {
        super(operationInvoker);
    }

    @Override
    public CompletableFuture<Void> signUp(SignUpDto signUpDto)
    {
        return operationInvoker.invoke(signUpDto, OperationsNames.SIGNUP);
    }

    @Override
    public CompletableFuture<UserDto> signIn(SignInDto signInDto)
    {
        return operationInvoker.invoke(signInDto,OperationsNames.SIGNIN);
    }

    @Override
    public CompletableFuture<UsersWrapperDto> loadUsers(UsersFilterDto filter)
    {
        return operationInvoker.invoke(filter,OperationsNames.LOAD_USERS);
    }

    @Override
    public void close()
    {

    }
}
