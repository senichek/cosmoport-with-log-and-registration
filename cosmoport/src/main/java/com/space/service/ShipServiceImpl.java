package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
@Transactional
public class ShipServiceImpl implements ShipService {

    Logger logger = Logger.getLogger(ShipServiceImpl.class.getName());

    private ShipRepository shipRepository;

    public ShipServiceImpl() {
    }

    @Autowired
    public ShipServiceImpl(ShipRepository shipRepository) {
        //super();
        this.shipRepository = shipRepository;
    }

    @Override
    public List<Ship> getAllShipsUnfiltered() {
        List<Ship> allShipsList = new ArrayList<>();
        Iterable<Ship> ships = shipRepository.findAll();
        for (Ship x : ships) {
            allShipsList.add(x);
        }

        logger.setLevel(Level.INFO);

        return allShipsList;

    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public void saveShip(Ship ship) {

        shipRepository.save(ship);

    }

    @Override
    public boolean shipExists(Long id) {

        for (Ship x : getAllShipsUnfiltered()) {
            if (x.getId() == id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Ship getShip(Long id) {

        return shipRepository.findById(id).get();
    }

    @Override
    public List<Ship> getShipsByFullOrPartialName(String name, List<Ship> ships) {

        /* Поиск по полям name и planet происходить по частичному соответствию. Например, если в БД есть корабль
        с именем «Левиафан», а параметр name задан как «иа» - такой корабль должен отображаться в результатах (Левиафан). */

        List<Ship> shipsFoundByName = new ArrayList<>();
        for (Ship x : ships) {
            // возвращает true если имя включает в себя последновательность символов name
            if (x.getName().contains(name)) {
                shipsFoundByName.add(x);
            }
        }
        return shipsFoundByName;
    }

    @Override
    public List<Ship> getShipsByFullOrPartialPlanetName(String name, List<Ship> ships) {
        List<Ship> shipsFoundByPlanetName = new ArrayList<>();
        for (Ship x : ships) {
            if (x.getPlanet().contains(name)) {
                shipsFoundByPlanetName.add(x);
            }
        }
        return shipsFoundByPlanetName;
    }

    public List<Ship> getShipsByType (ShipType type, List<Ship> ships) {

        List<Ship> shipsFoundByShipType = new ArrayList<>();

        for (Ship x : ships) {
            if (x.getShipType() == type) {
                shipsFoundByShipType.add(x);
            }
        }
        return shipsFoundByShipType;
    }

    @Override
    public List<Ship> getShipsBetweenProdDates(Long before, Long after, List<Ship> ships) {

        List<Ship> shipsBetweenTwoDates = new ArrayList<>();

        if (after != null && before == null) {
            // получаем год из переданного параметра after или before
            Date dateAfter = new Date(after);

            Calendar calendarAfter = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
            calendarAfter.setTime(dateAfter);

            int afterYear = calendarAfter.get(Calendar.YEAR);

            int shipProdYear;

            for (Ship x : ships) {
                // Получаем год создания корабля
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
                cal.setTime(x.getProdDate());
                shipProdYear = cal.get(Calendar.YEAR);
                if (shipProdYear > afterYear) {
                    shipsBetweenTwoDates.add(x);
                }
            }
        }

        if (before != null && after == null) {
            // получаем год из переданного параметра after или before
            Date dateBefore = new Date(before);

            Calendar calendarBefore = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
            calendarBefore.setTime(dateBefore);

            int beforeYear = calendarBefore.get(Calendar.YEAR);

            int shipProdYear;

            for (Ship x : ships) {
                // Получаем год создания корабля
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
                cal.setTime(x.getProdDate());
                shipProdYear = cal.get(Calendar.YEAR);
                if (shipProdYear <= beforeYear) {
                    shipsBetweenTwoDates.add(x);
                }
            }
        }

        if (before != null && after != null) {
            // получаем год из переданного параметра after или before
            Date dateBefore = new Date(before);
            Date dateAfter = new Date(after);

            Calendar calendarBefore = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
            Calendar calendarAfter = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));

            calendarBefore.setTime(dateBefore);
            calendarAfter.setTime(dateAfter);

            int beforeYear = calendarBefore.get(Calendar.YEAR);
            int afterYear = calendarAfter.get(Calendar.YEAR);

            int shipProdYear;

            for (Ship x : ships) {
                // Получаем год создания корабля
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
                cal.setTime(x.getProdDate());
                shipProdYear = cal.get(Calendar.YEAR);
                // Сравниваем год создания корабля с переданными в функцию параметрами
                if (shipProdYear > afterYear && shipProdYear <= beforeYear) {
                    shipsBetweenTwoDates.add(x);
                }
            }

        }

        return shipsBetweenTwoDates;
    }

    public List<Ship> getShipsBetweenMinAndMaxSpeedRange (Double minSpeed, Double maxSpeed, List<Ship> ships) {

        List<Ship> shipsBetweenMinAndMaxSpeedRange = new ArrayList<>();

        if (minSpeed != null && maxSpeed == null) {
            for (Ship x : ships) {
                if (x.getSpeed() >= minSpeed) {
                    shipsBetweenMinAndMaxSpeedRange.add(x);
                }
            }
        }

        if (maxSpeed != null && minSpeed == null) {
            for (Ship x : ships) {
                if (x.getSpeed() <= maxSpeed) {
                    shipsBetweenMinAndMaxSpeedRange.add(x);
                }
            }
        }

        if (maxSpeed != null && minSpeed != null) {
            for (Ship x : ships) {
                if (x.getSpeed() >= minSpeed && x.getSpeed() <= maxSpeed) {
                    shipsBetweenMinAndMaxSpeedRange.add(x);
                }
            }

        }

        return shipsBetweenMinAndMaxSpeedRange;
    }

    @Override
    public List<Ship> getShipsBetweenMinAndMaxCrewSize(Integer minCrewSize, Integer maxCrewSize, List<Ship> ships) {

        List<Ship> shipsBetweenMinAndMaxCrewSize = new ArrayList<>();

        if (minCrewSize != null && maxCrewSize == null) {
            for (Ship x : ships) {
                if (x.getCrewSize() >= minCrewSize) {
                    shipsBetweenMinAndMaxCrewSize.add(x);
                }
            }
        }

        if (maxCrewSize != null && minCrewSize == null) {
            for (Ship x : ships) {
                if (x.getCrewSize() <= maxCrewSize) {
                    shipsBetweenMinAndMaxCrewSize.add(x);
                }
            }
        }

        if (maxCrewSize != null && minCrewSize != null) {
            for (Ship x : ships) {
                if (x.getCrewSize() >= minCrewSize && x.getCrewSize() <= maxCrewSize) {
                    shipsBetweenMinAndMaxCrewSize.add(x);
                }
            }

        }

        return shipsBetweenMinAndMaxCrewSize;
    }

    @Override
    public List<Ship> getShipsBetweenMinAndMaxRating(Double minRating, Double maxRating, List<Ship> ships) {

        List<Ship> shipsBetweenMinAndMaxRating = new ArrayList<>();

        if (minRating != null && maxRating == null) {
            for (Ship x : ships) {
                if (x.getRating() >= minRating) {
                    shipsBetweenMinAndMaxRating.add(x);
                }
            }
        }

        if (maxRating != null && minRating == null) {
            for (Ship x : ships) {
                if (x.getRating() <= maxRating) {
                    shipsBetweenMinAndMaxRating.add(x);
                }
            }
        }

        if (maxRating != null && minRating != null) {
            for (Ship x : ships) {
                if (x.getRating() >= minRating && x.getRating() <= maxRating) {
                    shipsBetweenMinAndMaxRating.add(x);
                }
            }

        }

        return shipsBetweenMinAndMaxRating;
    }

    public List<Ship> getShipsByUsedUnusedParam(boolean used, List <Ship> ships) {
        List<Ship> usedOrUnusedShips = new ArrayList<>();
        if (used == true) {
            for (Ship x : ships) {
                if (x.getUsed() == true) {
                    usedOrUnusedShips.add(x);
                }
            }
        }

        if (used == false) {
            for (Ship x : ships) {
                if (x.getUsed() == false) {
                    usedOrUnusedShips.add(x);
                }
            }
        }

        return usedOrUnusedShips;
    }

    @Override
    public Ship updateShip(Long id, Ship ship) {

        /* Обновлять нужно только те поля, которые не null. Если корабль не найден в БД, необходимо ответить ошибкой
        с кодом 404. Если значение id не валидное, необходимо ответить ошибкой с кодом 400. */

        if (id == null || id == 0 ) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, id + " is invalid (it must not be zero or null;");

        }

        if (shipExists(id)) {

            Ship updatedShip = getShip(id);

            if (ship.getName() != null) {
                {
                    if (!ship.getName().isEmpty())
                        updatedShip.setName(ship.getName());
                }

                if (ship.getName().isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
                }
            }

            if (ship.getPlanet() != null) {
                {
                    if (!ship.getPlanet().isEmpty())
                        updatedShip.setPlanet(ship.getPlanet());
                }

                if (ship.getPlanet().isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
                }
            }

            if (ship.getShipType() != null) {
                updatedShip.setShipType(ship.getShipType());
            }

            if (ship.getProdDate() != null) {
                if (ship.getProdDate().getTime() < 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
                } else updatedShip.setProdDate(ship.getProdDate());
            }

            if (ship.getUsed() != null) {
                updatedShip.setUsed(ship.getUsed());
            }

            if (ship.getSpeed() != null) {
                updatedShip.setSpeed(ship.getSpeed());
            }

            if (ship.getCrewSize() != null) {
                if (ship.getCrewSize() < 300000000) {
                    updatedShip.setCrewSize(ship.getCrewSize());
                }
                if (ship.getCrewSize() < 0 || ship.getCrewSize() == null || ship.getCrewSize() >= 300000000) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
                }
            }


            // Пересчитываем рейтинг, так как параметры корабля могли обновиться.
            Double updatedRating = computeRating(updatedShip);

            updatedShip.setRating(updatedRating);

            logger.setLevel(Level.INFO);
            logger.info("Ship id=" + id + " was updated;");

            return updatedShip;
        }

        // При запросе POST /rest/ships/{id} с пустым телом запроса, корабль не должен изменяться.


        else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ship was not found;");
    }

    @Override
    public void deleteShip(Long id) {

        Ship shipToDelete = getShip(id);
        shipRepository.delete(shipToDelete);
        logger.setLevel(Level.INFO);
        logger.info("Ship id=" + id + " was deleted;");

    }

    @Override
    public List<Ship> sortShipsByOrder(List<Ship> ships, ShipOrder order) {

        /* При первом запуске приложения корабли по-умолчанию выстроены по Order By ID, поэтому если параметр order
        не был передан,то устанавливаем ему значение id, чтобы выстроить корабли по ID при первом запуске приложения. */
        String orderName = new String();
        if (order == null) {
            orderName = "id";
        } else orderName = order.getFieldName();

        // сортируем корабли по скорости
        switch (orderName) {
            case "speed":
                Collections.sort(ships, new Comparator<Ship>() {
                    @Override
                    public int compare(Ship o1, Ship o2) {
                        if (o1.getSpeed() > o2.getSpeed()) {
                            return 1;
                        }
                        if (o1.getSpeed() < o2.getSpeed()) {
                            return -1;
                        }
                        else return 0;
                    }
                });
                break;
            // сортируем корабли по дате производства
            case "prodDate":
                Collections.sort(ships, new Comparator<Ship>() {
                    @Override
                    public int compare(Ship o1, Ship o2) {
                        if (o2.getProdDate().before(o1.getProdDate())) {
                            return 1;
                        }
                        if (o2.getProdDate().after(o1.getProdDate())) {
                            return -1;
                        }
                        else return 0;
                    }
                });
                break;
            // сортируем корабли по рейтингу
            case "rating":
                Collections.sort(ships, new Comparator<Ship>() {
                    @Override
                    public int compare(Ship o1, Ship o2) {
                        return o1.getRating() > o2.getRating() ? 1 : (o1.getRating() < o2.getRating()) ? -1 : 0;
                    }
                });
                break;
            // сортируем корабли по ID
            case "id":
                Collections.sort(ships, new Comparator<Ship>() {
                    @Override
                    public int compare(Ship o1, Ship o2) {
                        if (o1.getId() > o2.getId()) {
                            return 1;
                        }
                        if (o1.getId() < o2.getId()) {
                            return -1;
                        }
                        else return 0;
                    }
                });
                break;
        }

        return ships;
    }

    @Override
    public List<Ship> getSublistBasedOnPageSizeAndPageNumber(List<Ship> ships, Integer pageNumber, Integer pageSize) {

        // Функция возвращает подписок кораблей на основе размера страницы и её номера.

        // indexFrom расчитываем по формуле pageNum * pageSize;
        // indexTO расчитываем по формуле indexFrom + pageSize;
        Integer indexTO = 0;
        Integer indexFrom = 0;


        // Если параметр pageNumber не указан – нужно использовать значение 0.
        if (pageNumber == null) {
            pageNumber = 0;
        }
        // Если параметр pageSize не указан – нужно использовать дефолтное значение 3.
        if (pageSize == null) {
            pageSize = 3;
        }

        /*но если получаем параметры PageSize и PageNumber, то выводим часть списка на основе indexFrom и indexTO.
        например, при размере страницы 5 и номере страницы 2 выводим список кораблей с 6 по 10 номер. */
        indexFrom = pageNumber * pageSize;
        indexTO = indexFrom + pageSize;

        /* indexTo не должен выходить за пределы подсписка. Если indexTo выходит за пределы списка, то
           переменной indexTO присваивается значение равное размеру списка*/
        if (indexTO > ships.size()) {
            indexTO = ships.size();
        } return ships.subList(indexFrom, indexTO);
    }

    @Override
    public boolean isShipValid(Ship ship) {
        /*
        Если в запросе на создание корабля нет параметра “isUsed”, то считаем, что пришло значение “false”.
        Мы не можем создать корабль, если:
        - указаны не все параметры из Data Params (кроме isUsed);
        - длина значения параметра “name” или “planet” превышает размер соответствующего поля в БД (50 символов);
        - значение параметра “name” или “planet” пустая строка;
        - скорость или размер команды находятся вне заданных пределов;
        - “prodDate”:[Long] < 0;
        - год производства находятся вне заданных пределов.
        В случае всего вышеперечисленного необходимо ответить ошибкой с кодом 400. */

        if (ship.getName() == null || ship.getPlanet() == null || ship.getName().length() == 0
                || ship.getPlanet().length() == 0 || ship.getName().length() > 50 ||
                ship.getPlanet().length() > 50 || ship.getShipType() == null || ship.getProdDate().getYear() < 0
                || ship.getCrewSize() >= 300000000 || ship.getSpeed() == null ) {

            return false;
        }

        return true;
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public Double computeRating(Ship ship) {

        /*
        v — скорость корабля;
        k — коэффициент, который равен 1 для нового корабля и 0,5 для использованного;
        y0 — текущий год (не забудь, что «сейчас» 3019 год);
        y1 — год выпуска корабля.
        */

        Double v = ship.getSpeed();
        Double k = 1.0;

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        calendar.setTime(ship.getProdDate());


        int shipProdYear = calendar.get(Calendar.YEAR);
        int currentYear = 3019;

        if (ship.getUsed()) {
            k = 0.5;
        }

        Double rating = (80 * v * k) / (currentYear - shipProdYear + 1);

        // округляем до сотых
        double roundedRating = round(rating, 2);

        return roundedRating;
    }

    @Override
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
            Integer pageSize) {

        /* Получаем список кораблей и фильтруем его по другим полям (по переданным параметрам,
           например, по макс. и мин. скорости). */

        List<Ship> filteredShips = getAllShipsUnfiltered();

        if (name != null) {
            filteredShips = getShipsByFullOrPartialName(name, filteredShips);
        }

        if (planet != null) {
            filteredShips = getShipsByFullOrPartialPlanetName(planet, filteredShips);
        }

        if (after != null || before != null) {
            filteredShips = getShipsBetweenProdDates(before, after, filteredShips);
        }

        if (minCrewSize != null || maxCrewSize != null) {
            filteredShips = getShipsBetweenMinAndMaxCrewSize(minCrewSize, maxCrewSize, filteredShips);
        }

        if (minSpeed != null || maxSpeed != null) {
            filteredShips = getShipsBetweenMinAndMaxSpeedRange(minSpeed, maxSpeed, filteredShips);
        }

        if (minRating != null || maxRating != null) {
            filteredShips = getShipsBetweenMinAndMaxRating(minRating, maxRating, filteredShips);
        }

        if (shipType != null) {
            filteredShips = getShipsByType(shipType, filteredShips);
        }

        if (isUsed != null) {
            filteredShips = getShipsByUsedUnusedParam(isUsed, filteredShips);
        }

        return filteredShips;
    }

}
