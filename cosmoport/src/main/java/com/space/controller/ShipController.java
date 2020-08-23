package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
public class ShipController {

    private ShipService shipService;

    public ShipController() {
    }

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @RequestMapping(path = "/rest/ships", method = RequestMethod.GET)
    public List<Ship> getAllShips (
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating,
            @RequestParam(value = "order", required = false) ShipOrder order,
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {


        /* Получаем список кораблей, отфильтрованный по переданным параметрам. Если параметры переданы не были,
        то возвращается список всех кораблей, находящихся в базе данных. */
        List<Ship> filteredShips = shipService.getFilteredShips(
                name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
                minCrewSize, maxCrewSize, minRating, maxRating, order, pageNumber, pageSize);

        // сортируем по полю Order
        List<Ship> sortedByOrder = shipService.sortShipsByOrder(filteredShips, order);

        // Возвращаем часть списка в зависимоости от размера страницы и ее номера.
        return shipService.getSublistBasedOnPageSizeAndPageNumber(sortedByOrder, pageNumber, pageSize);
    }

    @RequestMapping(path = "/rest/ships/count", method = RequestMethod.GET)
    public Integer getShipsCount (
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating,
            @RequestParam(value = "order", required = false) ShipOrder order,
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize
    )
    {

            // Получаем общее количество найденных кораблей.
            return shipService.getFilteredShips(name, planet, shipType, after, before,
                    isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize,
                    minRating, maxRating, order, pageNumber, pageSize).size();
        }

    @RequestMapping(path = "/rest/ships", method = RequestMethod.POST)
    public ResponseEntity<Ship> createShip (@RequestBody Ship ship) {

        /* ResponseEntity представляет полный HTTP-ответ: код состояния, заголовки и тело.
        Из-за этого мы можем использовать его для полной настройки HTTP-ответа. */

        if (!shipService.isShipValid(ship)) {
            return new ResponseEntity<>(ship, HttpStatus.BAD_REQUEST);
        } else {

            Ship shipToAddToDB = new Ship();
            shipToAddToDB.setName(ship.getName());
            shipToAddToDB.setPlanet(ship.getPlanet());
            shipToAddToDB.setProdDate(ship.getProdDate());
            shipToAddToDB.setCrewSize(ship.getCrewSize());
            shipToAddToDB.setSpeed(ship.getSpeed());
            shipToAddToDB.setShipType(ship.getShipType());

           // Если в запросе на создание корабля нет параметра “isUsed”, то считаем, что пришло значение “false”.
            if (ship.getUsed() == null) {
                shipToAddToDB.setUsed(false);
            } else {
                shipToAddToDB.setUsed(ship.getUsed());
            }


            shipToAddToDB.setRating(shipService.computeRating(shipToAddToDB));

            if (ship.getUsed() == null) {
                ship.setUsed(false);
            }

            shipService.saveShip(shipToAddToDB);

            return new ResponseEntity<>(shipToAddToDB, HttpStatus.OK);
        }

    }

    @RequestMapping(path = "/rest/ships/{id}", method = RequestMethod.GET)
    public ResponseEntity<Ship> getShip(@PathVariable("id") Long id) {

        /* Если корабль не найден в БД, необходимо ответить ошибкой с кодом 404.
        Если значение id не валидное, необходимо ответить ошибкой с кодом 400. */
        Ship ship = new Ship();

        if (id != null && id != 0 && shipService.shipExists(id)) {
            ship = shipService.getShip(id);
            return new ResponseEntity<>(ship, HttpStatus.OK);
        }

        if (id == null || id == 0) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The index must not be null or 0;");

        }

        else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ship with index " + id + " doesn't exist;");

    }

    @RequestMapping(path = "/rest/ships/{id}")
    public ResponseEntity<String> deleteShip(@PathVariable("id") Long id) {

        /* Если корабль не найден в БД, необходимо ответить ошибкой с кодом 404.
        Если значение id не валидное, необходимо ответить ошибкой с кодом 400. */

        if (id != null && id != 0 && shipService.shipExists(id)) {
            shipService.deleteShip(id);
            return new ResponseEntity<>("Ship was deleted;", HttpStatus.OK);
        }

        if (id == null || id == 0 ) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, id + " is invalid (it must not be zero or null;");

        }

        else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ship was not found;");

    }

    @RequestMapping(path = "/rest/ships/{id}", method = RequestMethod.POST)
    public ResponseEntity<Ship> updateShip(@PathVariable("id") Long id, @RequestBody Ship ship) {

        return new ResponseEntity<>(shipService.updateShip(id, ship), HttpStatus.OK);

    }
}
