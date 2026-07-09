package sev.amorlov.plant_nursery.model;

public enum Role {
    ROLE_CLIENT,   // Может только смотреть товары
    ROLE_MANAGER,  // Может оформлять заказы, управлять поставками
    ROLE_ADMIN     // Полный доступ к системе
}