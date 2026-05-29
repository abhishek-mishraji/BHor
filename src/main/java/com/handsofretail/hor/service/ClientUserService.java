package com.handsofretail.hor.service;

import java.util.List;

import com.handsofretail.hor.dto.request.ClientUserRequest;
import com.handsofretail.hor.dto.request.ClientUserUpdateRequest;
import com.handsofretail.hor.dto.response.ClientUserResponse;

public interface ClientUserService {

    ClientUserResponse createClient(ClientUserRequest request);

    List<ClientUserResponse> getAllClients();

    ClientUserResponse updateClient(Long id, ClientUserUpdateRequest request);

}