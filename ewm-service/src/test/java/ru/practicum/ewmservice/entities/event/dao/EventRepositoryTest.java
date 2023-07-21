package ru.practicum.ewmservice.entities.event.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// @ActiveProfiles("default")
// @Rollback(value = false)
class EventRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private EventRepository repository;

    /*@Test
    void distanceMethodTest() {
        EntityManager em = testEntityManager.getEntityManager();
        User initiator = User.builder()
                .name("Alex")
                .email("alex@mail.com")
                .build();
        em.persist(initiator);

        Category category = Category.builder()
                .name("Concert")
                .build();
        em.persist(category);

        Location location = Location.builder()
                .lat(58.317f)
                .lon(69.317f)
                .build();

        Event event = Event.builder()
                .annotation("some annot")
                .category(category)
                .confirmedRequests(0)
                .description("some desc")
                .eventDate(LocalDateTime.now().plusHours(10))
                .location(location)
                .paid(false)
                .participantLimit(800)
                .requestModeration(false)
                .confirmedRequests(0)
                .title("Concert Rock")
                .initiator(initiator)
                .build();

        em.persist(event);

        List<Event> resultList = em.createNativeQuery("SELECT * FROM events as ev " +
                        "WHERE distance(ev.lat,ev.lon,58.317,69.317) = 0", Event.class)
                .getResultList();

        BooleanExpression expr = Expressions.numberTemplate(Float.class,
                        "distance({0},{1},{2},{3})", QEvent.event.location.lat, QEvent.event.location.lon, 57.315, 68.310)
                .subtract(100)
                .loe(0);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(expr);
        BooleanExpression query = Expressions
                .booleanTemplate("distance({0},{1},{2},{3})", 58.317f, 58.317f, 58.317f, 69.317f)
                .goe(eventPath.location.lat);

        Iterable<Event> events = repository.findAll(ExpressionUtils.allOf(predicates));
        System.out.println(events);
    }*/

}