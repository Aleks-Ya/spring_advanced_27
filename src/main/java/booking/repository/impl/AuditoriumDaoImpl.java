package booking.repository.impl;

import booking.domain.Auditorium;
import booking.repository.AuditoriumDao;

import java.util.List;
import java.util.Optional;

public class AuditoriumDaoImpl extends AbstractDao implements AuditoriumDao {

    @Override
    @SuppressWarnings("unchecked")
    public List<Auditorium> getAll() {
        return ((List<Auditorium>) createBlankCriteria(Auditorium.class).list());
    }

    @Override
    public Optional<Auditorium> getById(Long auditoriumId) {
        return Optional.ofNullable(getCurrentSession().get(Auditorium.class, auditoriumId));
    }

    @Override
    public void delete(Long auditoriumId) {
        getById(auditoriumId).ifPresent(auditorium -> getCurrentSession().delete(auditorium));
    }

    @Override
    public Auditorium create(Auditorium auditorium) {
        getCurrentSession().save(auditorium);
        return auditorium;
    }
}
