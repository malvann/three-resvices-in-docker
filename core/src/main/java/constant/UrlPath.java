package constant;

public class UrlPath {
    public static class DeliveryUrl {
        public static final String DOM_URL = "/tsid/delivery";
        public static final String RECEIVE_ORDERS_URL = DOM_URL + "/receiveOrders";
    }

    public static class KitchenUrl {
        public static final String DOM_URL = "/tsid/kitchen";
        public static final String RECEIVE_ORDERS_URL = DOM_URL + "/receiveOrders";
        public static final String DAY_DISH_SET = DOM_URL + "/dayDishSet";
    }

    public static class RestaurantUrl {
        public static final String DOM_URL = "/tsid/restaurant";
        public static final String GET_DONE_KITCHEN_URL = DOM_URL + "/getDoneKitchenOrder";
        public static final String GET_DONE_DELIVERY_URL = DOM_URL + "/getDoneDeliveryOrder";
        public static final String ORDER_STATUS_URL = DOM_URL + "/order";
    }
}
