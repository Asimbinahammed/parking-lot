package com.bridgelab;

import java.util.ArrayList;
import java.util.List;

/**
 * Purpose : To check availability of slot for parking,
 * to park if slot is free,
 * to unpark from the slot.
 *
 * @author : ASIM AHAMMED
 * @since : 09-11-2021
 */
public class ParkingLotSystem {
    public static List<Vehicle> parkingLot1;
    public static List<Vehicle> parkingLot2;
    public static List<Vehicle> handicappedLot;
    private static int actualCapacity;
    private static List<ParkingLotObserver> observers;
    private static int rowSize =2;
    private Vehicle Vehicle;

    public ParkingLotSystem() {
        observers = new ArrayList<>();
        parkingLot1 = new ArrayList<>(actualCapacity);
        parkingLot2 = new ArrayList<>(actualCapacity);
        handicappedLot = new ArrayList<>(actualCapacity);
    }

    /**
     * Purpose : Setting observer
     *
     * @param observer : parking lot observer
     */
    public void setParkingLotObserver(ParkingLotObserver observer) {
        observers.add(observer);
    }

    /**
     * Purpose : setting new capacity for lot
     *
     * @param capacity : maximum limit of parking lot
     */
    public void setCapacity(int capacity) {
        actualCapacity = capacity;
    }

    /**
     * Purpose : parks vehicle if slot is free. Inform police when certain requirements meets while parking.
     *
     * @param vehicle for park
     * @throws ParkingLotException : When parking lot is full or when vehicle is not present.
     */
    public void park(Vehicle vehicle) throws ParkingLotException {
        if (parkingLot1.size() == actualCapacity && parkingLot2.size() == actualCapacity &&
                handicappedLot.size() == actualCapacity) {
            throw new ParkingLotException(ParkingLotException.ExceptionType.PARKING_LOT_IS_FULL, "Lot is full");
        }
        if (isVehicleParked(vehicle))
            throw new ParkingLotException(ParkingLotException.ExceptionType.VEHICLE_ALREADY_PARKED,
                    "Vehicle already parked");
        if (vehicle.isHandicapped()) {
            if (handicappedLot.size() > actualCapacity)
                throw new ParkingLotException(ParkingLotException.ExceptionType.HANDICAP_PARKING_LOT_IS_FULL,
                        "Handicap parking lot is full");
            handicappedLot.add(vehicle);
            Police.listHandicapped(vehicle);
        }
        if (!vehicle.isHandicapped()) {
            if (parkingLot1.size() > parkingLot2.size()) {
                parkingLot2.add(vehicle);
            } else
                parkingLot1.add(vehicle);
        }
        ParkingLotOwner.parkedTime(vehicle);
        checkCapacity();
        policeChecks(vehicle);
    }

    /**
     * Purpose : share sorted data into police class
     *
     * @param vehicle : parked vehicle or further sorting
     * @throws ParkingLotException : When vehicle is not present
     */
    private void policeChecks(Vehicle vehicle) throws ParkingLotException {
        if (vehicle.getColor().equals("white"))
            Police.getAllWhiteCars(getPositionByColor("white"));
        if (vehicle.getColor().equals("blue") && vehicle.getVehicle().equals("Toyota"))
            Police.getAllToyotaBlueCar(vehicle);
        if (vehicle.getVehicle().equals("BMW"))
            Police.listOfBMW(findVehicle(vehicle));
    }

    /**
     * Purpose : check whether lot is full or not if full then passing into observers
     */
    private void checkCapacity() {
        if (parkingLot1.size() == actualCapacity && parkingLot2.size() == actualCapacity &&
                handicappedLot.size() == actualCapacity) {
            for (ParkingLotObserver observer : observers) {
                observer.capacityFull();
            }
        }
    }

    /**
     * Purpose : Unpark vehicle from slot
     *
     * @param vehicle to unpark
     * @return boolean : true if vehicle can unpark
     * @throws ParkingLotException : When vehicle is not present
     */
    public boolean unPark(Vehicle vehicle) throws ParkingLotException {
        if (parkingLot1 == null || parkingLot2 == null || handicappedLot == null) return false;
        for (Vehicle slot : parkingLot1) {
            if (slot.equals(vehicle)) {
                parkingLot1.remove(vehicle);
                for (ParkingLotObserver observer : observers) {
                    observer.capacityAvailable();
                }
                ParkingLotOwner.removeFromList(vehicle);
                return true;
            }
        }
        for (Vehicle slot : parkingLot2) {
            if (slot.equals(vehicle)) {
                parkingLot2.remove(vehicle);
                for (ParkingLotObserver observer : observers) {
                    observer.capacityAvailable();
                }
                ParkingLotOwner.removeFromList(vehicle);
                return true;
            }
        }
        for (Vehicle slot : handicappedLot) {
            if (slot.equals(vehicle)) {
                handicappedLot.remove(vehicle);
                for (ParkingLotObserver observer : observers) {
                    observer.capacityAvailable();
                }
                ParkingLotOwner.removeFromList(vehicle);
                return true;
            }
        }
        throw new ParkingLotException(ParkingLotException.ExceptionType.NO_SUCH_VEHICLE, "No Such Vehicle Found");
    }

    /**
     * Purpose : To find spot of vehicle if vehicle is present in parking lot.
     *
     * @param vehicle: to crosscheck with parked list to find parked spot
     * @return spot number of vehicle
     * @throws ParkingLotException : When vehicle is not present
     */
    public static int findVehicle(Vehicle vehicle) throws ParkingLotException {
        for (Vehicle slot : parkingLot1) {
            if (slot.equals(vehicle))
                return parkingLot1.indexOf(slot) + 1;
        }
        for (Vehicle slot : parkingLot2) {
            if (slot.equals(vehicle))
                return parkingLot2.indexOf(slot) + 1;
        }
        for (Vehicle slot : handicappedLot) {
            if (slot.equals(vehicle))
                return handicappedLot.indexOf(slot) + 1;
        }
        throw new ParkingLotException(ParkingLotException.ExceptionType.NO_SUCH_VEHICLE,
                "Vehicle is not present in parking lot");
    }

    /**
     * purpose : to check vehicle is parked or not
     *
     * @param vehicle : to check from each slot parked list
     * @return boolean : if parked true or else false
     */
    public boolean isVehicleParked(Vehicle vehicle) {
        boolean isParked = false;
        for (Vehicle slot : parkingLot1)
            if (slot.equals(vehicle)) {
                isParked = true;
                break;
            }
        for (Vehicle slot : parkingLot2) {
            if (slot.equals(vehicle)) {
                isParked = true;
                break;
            }
        }
        for (Vehicle slot : handicappedLot) {
            if (slot.equals(vehicle)) {
                isParked = true;
                break;
            }
        }
        return isParked;
    }

    /**
     * purpose : to find spot of vehicle with specific color
     *
     * @param color : mentioning color tobe filtered out
     * @return spot number
     * @throws ParkingLotException : when o such vehicle found
     */
    public int getPositionByColor(String color) throws ParkingLotException {
        for (Vehicle slot : parkingLot1) {
            if (slot.getColor().equals(color))
                return parkingLot1.indexOf(slot) + 1;
        }
        for (Vehicle slot : parkingLot2) {
            if (slot.getColor().equals(color))
                return parkingLot2.indexOf(slot) + 1;
        }
        for (Vehicle slot : handicappedLot) {
            if (slot.getColor().equals(color))
                return handicappedLot.indexOf(slot) + 1;
        }
        throw new ParkingLotException(ParkingLotException.ExceptionType.NO_SUCH_VEHICLE, "No such vehicle found");
    }

    /**
     * Purpose : to calculate which spot tobe considered when array are specified.
     *
     * @param row1 : row number
     * @return spots : array consists of parking spots
     */
    public static int[] vehicleInRow(int row1) {
        int spot1 = row1 * rowSize - 2;
        int spot2 = row1 * rowSize - 1;
        int[] spots = {spot1, spot2};
        return (spots);
    }

}