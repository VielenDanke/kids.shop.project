package kz.danke.kids.shop.document;

public enum Height {
    THIRTY_SIX("50-56"),
    FORTY("56-62"),
    FORTY_FOUR("62-68"),
    FORTY_EIGHT("68-74"),
    FIFTY_TWO("74-80"),
    FIFTY_SIX_FIRST("86-92"),
    FIFTY_SIX_SECOND("92-98"),
    SIXTY_FIRST("98-104"),
    SIXTY_SECOND("104-110"),
    SIXTY_THIRD("110-116"),
    SIXTY_FOUR("116-122"),
    SIXTY_EIGHT("122-128"),
    SIXTY_EIGHT_SEVENTY_TWO("128-134"),
    SEVENTY_TWO("134-140");

    private final String height;

    private Height(String height) {
        this.height = height;
    }

    public String height() {
        return this.height;
    }
}
