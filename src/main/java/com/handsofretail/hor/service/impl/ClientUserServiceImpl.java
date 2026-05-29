package com.handsofretail.hor.service.impl;

import com.handsofretail.hor.dto.request.ClientUserRequest;
import com.handsofretail.hor.dto.request.ClientUserUpdateRequest;
import com.handsofretail.hor.dto.response.ClientUserResponse;
import com.handsofretail.hor.entity.ClientUser;
import com.handsofretail.hor.enums.Status;
import com.handsofretail.hor.enums.UserRole;
import com.handsofretail.hor.exception.DuplicateResourceException;
import com.handsofretail.hor.exception.ResourceNotFoundException;
import com.handsofretail.hor.mapper.ClientUserMapper;
import com.handsofretail.hor.repository.ClientUserRepository;
import com.handsofretail.hor.service.ClientUserService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientUserServiceImpl implements ClientUserService {

        private final ClientUserRepository clientUserRepository;

        private final PasswordEncoder passwordEncoder;

        @Override
        public ClientUserResponse createClient(ClientUserRequest request) {

                boolean emailExists = clientUserRepository
                                .existsByEmail(request.getEmail());

                if (emailExists) {
                        throw new DuplicateResourceException("Email already exists");
                }

                ClientUser clientUser = ClientUser.builder()
                                .fullName(request.getFullName())
                                .email(request.getEmail())
                                .passwordHash(
                                                passwordEncoder.encode(request.getPassword()))
                                .phoneNumber(request.getPhoneNumber())
                                .address(request.getAddress())
                                .status(Status.ACTIVE)
                                .role(UserRole.CLIENT)
                                .build();

                ClientUser savedClient = clientUserRepository
                                .save(clientUser);

                return ClientUserMapper.toResponse(savedClient);
        }

        @Override
        public List<ClientUserResponse> getAllClients() {

                return clientUserRepository.findAll()
                                .stream()
                                .map(ClientUserMapper::toResponse)
                                .toList();
        }

        @Override
        public ClientUserResponse updateClient(Long id, ClientUserUpdateRequest request) {

                ClientUser client = clientUserRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

                // If email provided and different, check uniqueness
                if (request.getEmail() != null && !request.getEmail().equals(client.getEmail())) {
                        boolean emailExists = clientUserRepository.existsByEmail(request.getEmail());
                        if (emailExists) {
                                throw new DuplicateResourceException("Email already exists");
                        }
                        client.setEmail(request.getEmail());
                }

                if (request.getFullName() != null) {
                        client.setFullName(request.getFullName());
                }

                if (request.getPassword() != null && !request.getPassword().isBlank()) {
                        client.setPasswordHash(passwordEncoder.encode(request.getPassword()));
                }

                if (request.getPhoneNumber() != null) {
                        client.setPhoneNumber(request.getPhoneNumber());
                }

                if (request.getAddress() != null) {
                        client.setAddress(request.getAddress());
                }

                ClientUser updated = clientUserRepository.save(client);

                return ClientUserMapper.toResponse(updated);
        }

}