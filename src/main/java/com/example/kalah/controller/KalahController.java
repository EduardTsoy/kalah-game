package com.example.kalah.controller;

import com.example.kalah.KalahException;
import com.example.kalah.domain.ActivePlayer;
import com.example.kalah.domain.KalahGameEntity;
import com.example.kalah.dto.KalahGameDTO;
import com.example.kalah.service.KalahService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Objects;

import static com.example.kalah.service.KalahService.convertStringToByteArray;

@RestController
@RequestMapping("/kalah")
public class KalahController {
    private final static Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final KalahService kalahService;

    public KalahController(final KalahService kalahService) {
        this.kalahService = kalahService;
    }

    @Autowired

    @GetMapping("")
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>(
                "Kalah game server is alive!" +
                        "<br/><br/>" +
                        "Make a POST request to start a new game.",
                HttpStatus.OK);
    }

    @PostMapping("")
    @Transactional
    public ResponseEntity<KalahGameDTO> create(
            @RequestParam(required = false, defaultValue = "6") Byte pits,
            @RequestParam(required = false, defaultValue = "6") Byte stones) {
        final ResponseEntity<KalahGameDTO> result;
        final KalahGameEntity entity = kalahService.createGame(pits, stones);
        log.debug("\n // game: {}", entity);
        final KalahGameDTO dto = convertModelToDTO(entity);
        result = ResponseEntity.created(buildLocation("/kalah/" + entity.getId()))
                               .body(dto);
        return result;
    }

    @GetMapping(value = "/{gameId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<KalahGameDTO> getGame(@PathVariable final Long gameId) {
        final ResponseEntity<KalahGameDTO> result;
        final KalahGameEntity entity = kalahService.findGame(gameId);
        log.debug("\n // game: {}", entity);
        if (entity != null) {
            final KalahGameDTO dto = convertModelToDTO(entity);
            result = ResponseEntity.ok(dto);
        } else {
            result = ResponseEntity.notFound()
                                   .location(buildLocation(""))
                                   .build();
        }
        return result;
    }

    @PutMapping(value = "/{gameId}/{version}/{player}/{pitIndex}")
    @Transactional
    public ResponseEntity<KalahGameDTO> move(@PathVariable final Long gameId,
                                             @PathVariable final Integer version,
                                             @PathVariable final ActivePlayer player,
                                             @PathVariable final Integer pitIndex) {
        final ResponseEntity<KalahGameDTO> result;
        final KalahGameEntity game = kalahService.findGame(gameId);
        if (game == null) {
            final String message = "Sorry, there is no game with this ID (" + gameId + ")";
            throw new KalahException(message);
        }
        if (!Objects.equals(version, game.getVersion())) {
            final String message = "Sorry, " + player + ", the game situation slightly changed while you were thinking. Please click 'Refresh' button and then try again.";
            kalahService.addGameLog(game, message);
        } else {
            kalahService.makeMove(game, player, pitIndex - 1);
        }

        // We need to saveAndFlush the entity explicitly in order to obtain its new 'version'
        kalahService.saveAndFlush(game);

        final KalahGameDTO dto = convertModelToDTO(game);
        result = ResponseEntity.ok()
                               .location(buildLocation("/kalah/" + gameId))
                               .body(dto);
        return result;
    }

    /* ----------------
     * PRIVATE METHODS
     */

    private URI buildLocation(final String path) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/" + path)
                .build()
                .toUri();
    }

    private static KalahGameDTO convertModelToDTO(final KalahGameEntity model) {
        return model != null
               ? new KalahGameDTO(
                model.getId(),
                model.getVersion(),
                model.getNumberOfSmallPitsForEachPlayer(),
                model.getInitialNumberOfStonesInEachPit(),
                convertStringToByteArray(model.getFirstPits()),
                model.getFirstStore(),
                convertStringToByteArray(model.getSecondPits()),
                model.getSecondStore(),
                model.getActivePlayer().toString(),
                model.getFirstMessage(),
                model.getSecondMessage(),
                model.getEventLog().split("\n"),
                model.getGameOverMessage())
               : null;
    }

}
