package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;

import java.util.Date;
import java.util.List;

public interface ShipService {

    // Получаем список кораблей без какой-либо фильтрации или сортировки
    List<Ship> getAllShipsUnfiltered();

    void saveShip(Ship ship);

    Ship getShip(Long id);

    List<Ship> getShipsByFullOrPartialName(String name, List<Ship> ships);

    List<Ship> getShipsByFullOrPartialPlanetName(String name, List<Ship> ships);

    public List<Ship> getShipsBetweenProdDates(Long before, Long after, List<Ship> ships);

    public List<Ship> getShipsBetweenMinAndMaxCrewSize(Integer minCrewSize, Integer maxCrewSize, List<Ship> ships);

    public List<Ship> getShipsBetweenMinAndMaxSpeedRange(Double minSpeed, Double maxSpeed, List<Ship> ships);

    public List<Ship> getShipsBetweenMinAndMaxRating(Double minRating, Double maxRating, List<Ship> ships);

    public List<Ship> getShipsByType(ShipType type, List<Ship> ships);

    public List<Ship> getShipsByUsedUnusedParam(boolean used, List <Ship> ships);

    public Double computeRating (Ship ship);

    public double round(double value, int places);

    public boolean shipExists(Long id);

    Ship updateShip(Long id, Ship ship);

    void deleteShip(Long id);

    public List<Ship> getFilteredShips(
            String name,
            String planet,
            ShipType shipType,
            Long after,
            Long before,
            Boolean isUsed,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating,
            ShipOrder order,
            Integer pageNumber,
            Integer pageSize
    );

    List<Ship> sortShipsByOrder(List<Ship> ships, ShipOrder order);

    List<Ship> getSublistBasedOnPageSizeAndPageNumber(List<Ship> ships, Integer pageNumber, Integer pageSize);

    boolean isShipValid(Ship ship);

}
