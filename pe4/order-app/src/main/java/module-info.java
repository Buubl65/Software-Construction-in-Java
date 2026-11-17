module order.app {
    requires order.processing;
    requires order.threading;
    requires javafaker;
    // SnakeYAML, який використовує JavaFaker, завантажує java.sql.Date.
    requires java.sql;
}
