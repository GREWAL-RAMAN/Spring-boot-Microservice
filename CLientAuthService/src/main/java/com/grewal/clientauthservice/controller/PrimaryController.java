package com.grewal.clientauthservice.controller;

import com.grewal.clientauthservice.dto.*;
import com.grewal.clientauthservice.exception.*;
import com.grewal.clientauthservice.model.JwtResponse;
import com.grewal.clientauthservice.model.MyClaims;
import com.grewal.clientauthservice.model.Response;
import com.grewal.clientauthservice.security.JWTHelper;
import com.grewal.clientauthservice.service.ApplciationService;
import com.grewal.clientauthservice.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PrimaryController {

    private final ClientService clientService;
    private final ApplciationService applicationService;

    private final JWTHelper jwtHelper;

    @PostMapping("/primary/add")
    public ResponseEntity<Response> register(@RequestBody PrimaryDto client) throws ApplicationAlreadyExistException, ClientAlreadyExist {

        clientService.register(client);
        Response response=Response.builder().message("Success").build();
        return ResponseEntity.ok()
                .body(response);
    }
    @PostMapping("/secondary/add")
    public ResponseEntity<String> registerSecondary(
            @RequestBody SecondaryClientDto client,@RequestHeader(value = "x-api-key") String apiKey)
            throws ClientAlreadyExist {
        if(clientService.validateApiKey(apiKey)) {
            clientService.registerSecondary(client);
            return ResponseEntity.ok().body("Success");
        }
        else{
            return ResponseEntity.internalServerError().body("Api Key invalid");
        }
    }
    @PostMapping("/primary/application")
    public ResponseEntity<String> addApplication(@RequestBody ApplicationDto client)
            throws ApplicationAlreadyExistException, ClientAlreadyExist, ClientDoesNotExist {

        clientService.AddApplication(client);
        return ResponseEntity.ok()
                .body(client.getApplication()+" was successfully created");
    }

    @GetMapping("primary/application")
    public List<ApplicationResponse> getAllApplication(@RequestHeader(value="email") String email) throws ClientDoesNotExist {

        return clientService.getAllApplication(email);
    }

    @PostMapping("/secondary/login")
    public ResponseEntity<JwtResponse> loginSecondary(
            @RequestBody PrimaryValidateDto client,
            @RequestHeader(value = "x-api-key") String apiKey) throws EmailIncorrect, PasswordIncorrect, ApiKeyIncorrect {
                if(clientService.validateApiKey(apiKey))
                {
                    MyClaims claims = clientService.validateSecondary(client);

                    String token =jwtHelper.generateToken(claims);
                    return ResponseEntity.ok()
                            .body(JwtResponse.builder()
                                    .message("success")
                                    .token(token).build());
                }
                else{
                    throw new ApiKeyIncorrect();
                }

    }

    @PostMapping("/primary/login")
    public ResponseEntity<JwtResponse> login(@RequestBody PrimaryValidateDto client)
            throws
            ApiKeyIncorrect,EmailIncorrect,PasswordIncorrect {
        MyClaims userClaims =clientService.ValidatePrimaryClient(client);
        String token =jwtHelper.generatePrimaryToken(userClaims);
        return ResponseEntity.ok()
                .body(JwtResponse.builder()
                        .message("success")
                        .email(userClaims.getUserEmail())
                        .token(token).build());
    }
    @PostMapping("/primary/application/login")
    public ResponseEntity<JwtResponse> loginForPrimary(
            @RequestBody PrimaryValidateDto client,
            @RequestHeader(value = "x-api-key") String apiKey)
            throws
            ApiKeyIncorrect,EmailIncorrect,PasswordIncorrect {
        MyClaims userClaims =clientService.ValidateClient(client,apiKey);
        String token =jwtHelper.generateToken(userClaims);
        return ResponseEntity.ok()
                .body(JwtResponse.builder()
                        .message("success")
                        .email(userClaims.getUserEmail())
                        .token(token).build());
    }

    @PostMapping("/user/validate")
    public ResponseEntity<String> validate(@RequestHeader("x-api-key") String apiKey,
                                                     @RequestHeader("Authorization") String token

    )
    {
        MyClaims myClaims =jwtHelper.GetMyClaims(token);

        if (jwtHelper.isTokenExpired(token))
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token Expired");
        }
        else {
            if(clientService.validateUserAndApiKey(apiKey,myClaims))
            {
                return ResponseEntity.status(HttpStatus.OK).body(myClaims.getApplication());
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorised User");
            }
        }

    }

    @PostMapping("/user/client/validate")
    public ResponseEntity<String> ClientValidate(@RequestHeader("x-api-key") String apiKey,
                                           @RequestHeader("Authorization") String token
    )
    {

        MyClaims myClaims =jwtHelper.GetMyClaims(token);

        if (jwtHelper.isTokenExpired(token))
        {
            log.warn("user/client/validate  :expired called");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token Expired");
        }
        else {
            if(clientService.validateMainUserAndApiKey(apiKey,myClaims))
            {
                log.warn("user/client/validate {} :authorizaed called",myClaims.getApplication());
                return ResponseEntity.status(HttpStatus.OK).body(myClaims.getApplication());
            }
            else{
                log.warn("user/client/validate  :unauthorizaed called");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorised User");
            }
        }

    }

    @PostMapping("/user/secondary/validate")
    public ResponseEntity<String> SecondaryValidate(@RequestHeader("x-api-key") String apiKey,
                                                 @RequestHeader("Authorization") String token
    )
    {

        MyClaims myClaims =jwtHelper.GetMyClaims(token);

        if (jwtHelper.isTokenExpired(token))
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token Expired");
        }
        else {
            if(clientService.validateSecondaryUserAndApiKey(apiKey,myClaims))
            {

                String application = applicationService.getApplication(apiKey).get().getId();
                System.out.println(application);
                return ResponseEntity.status(HttpStatus.OK).body(application);
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorised User");
            }
        }

    }

    @PostMapping("/sample/validate")
    public String SAmpleValidate(@RequestHeader("x-api-key") String apiKey,
                                                    @RequestHeader("Authorization") String token
    )
    {

        MyClaims myClaims =jwtHelper.GetMyClaims(token);

        return myClaims.getApplication();
    }
    @PostMapping("/sample/login")
    public ResponseEntity<JwtResponse> SampleLogin(@RequestBody PrimaryValidateDto client)
            throws
            ApiKeyIncorrect,EmailIncorrect,PasswordIncorrect {
        MyClaims userClaims =clientService.ValidatePrimaryClient(client);
        String token =jwtHelper.generatePrimaryToken(userClaims);
        return ResponseEntity.ok()
                .body(JwtResponse.builder()
                        .message("success")
                        .email(userClaims.getUserEmail())
                        .token(token).build());
    }




    @ExceptionHandler(ApplicationAlreadyExistException.class)
    public ResponseEntity<String> handleApplicationException(ApplicationAlreadyExistException ex) {
        return new ResponseEntity<>("Exception:Application name already Exist", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ClientAlreadyExist.class)
    public ResponseEntity<String> handleClientException(ClientAlreadyExist ex) {
        return new ResponseEntity<>("Client Exist Exception: Email Already Exist" , HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(ApiKeyIncorrect.class)
    public ResponseEntity<String> handleApiException(ApiKeyIncorrect ex) {
        return new ResponseEntity<>("Api key incorrect" , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EmailIncorrect.class)
    public ResponseEntity<String> handleEmailIncorrectException(ClientAlreadyExist ex) {
        return new ResponseEntity<>("Incorrect Email" , HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(PasswordIncorrect.class)
    public ResponseEntity<String> handlePasswordIncorrectException(ApiKeyIncorrect ex) {
        return new ResponseEntity<>("Password Incorrect" , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ClientDoesNotExist.class)
    public ResponseEntity<String> handleClientDoesNotExistException(ClientDoesNotExist ex) {
        return new ResponseEntity<>("Client Does Not Exist" , HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
