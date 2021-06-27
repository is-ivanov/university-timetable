package ua.com.foxminded.university.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.com.foxminded.university.dao.interfaces.RoomDao;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.service.interfaces.RoomService;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.exception.ServiceException;

@Service
public class RoomServiceImpl implements RoomService {

    private RoomDao roomDao;

    @Autowired
    public RoomServiceImpl(RoomDao roomDao) {
        this.roomDao = roomDao;
    }

    @Override
    public void add(Room room) {
        roomDao.add(room);
    }

    @Override
    public Room getById(int id) throws ServiceException {
        Room room = null;
        try {
            room = roomDao.getById(id).orElse(new Room());
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
        return room;
    }

    @Override
    public List<Room> getAll() {
        return roomDao.getAll();
    }

    @Override
    public void update(Room room) {
        roomDao.update(room);
    }

    @Override
    public void delete(Room room) {
        roomDao.delete(room);
    }

}
