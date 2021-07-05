package io.opensaber.claim.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.opensaber.claim.entity.Claim;
import io.opensaber.claim.service.ClaimService;
import io.opensaber.pojos.dto.ClaimDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.opensaber.claim.contants.AttributeNames.ATTESTOR_INFO;
import static io.opensaber.claim.contants.AttributeNames.LOWERCASE_ENTITY;

@Controller
public class ClaimsController {

    private final ClaimService claimService;
    private static final Logger logger = LoggerFactory.getLogger(ClaimsController.class);

    @Autowired
    public ClaimsController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @RequestMapping(value = "/api/v1/getClaims", method = RequestMethod.POST)
    public ResponseEntity<List<Claim>> getClaims(@RequestHeader HttpHeaders headers,
                                                 @RequestBody JsonNode requestBody) {
        String entity = requestBody.get(LOWERCASE_ENTITY).asText();
        JsonNode attestorNode = requestBody.get(ATTESTOR_INFO);
        List<Claim> claims = claimService.findClaimsForAttestor(entity, attestorNode);
        return new ResponseEntity<>(claims, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/v1/claims", method = RequestMethod.POST)
    public ResponseEntity<Claim> save(@RequestBody ClaimDTO claimDTO) {
        logger.info("Adding new claimDTO {} ",  claimDTO.toString());
        Claim savedClaim = claimService.save(Claim.fromDTO(claimDTO));
        return new ResponseEntity<>(savedClaim, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/v1/claims/{claimId}", method = RequestMethod.POST)
    public ResponseEntity<Object> attestClaims(@PathVariable String claimId, @RequestBody JsonNode requestBody) {
        logger.info("Attesting claim : {}", claimId);
        return claimService.attestClaim(
                claimId,
                requestBody.get(ATTESTOR_INFO),
                requestBody
        );
    }

}