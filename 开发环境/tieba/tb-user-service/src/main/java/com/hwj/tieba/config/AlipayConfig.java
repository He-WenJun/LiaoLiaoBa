package com.hwj.tieba.config;

public class AlipayConfig {
    /**
     * 支付宝网关（固定）
     */
    public static final  String URL="https://openapi.alipay.com/gateway.do";

    /**
     * 授权url
     */
    public static final  String ALIPAY_URL = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm";

    /**
     * 应用id
     */
    public static final  String APP_ID="2021001160603018";

    /**
     * 应用私钥
     */
    public static final  String APP_PRIVATE_KEY="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCJRuUNxS7uuaMj8S2hXnXzeLYrnKa4TpWtURMSIqxyrN+PENFJe1wmTld4h+S2TiOIlBzRaMy96kMJ6qV0BU9c5/zSbJwQoQwRQmqJk2R689qKYDJBL/9gNt7pztYGRIiTT0X1qGWwXAxZfG61lGRWPzfY9CNFfM3gdxxzNfj37P6w7EXmw83538qH/flf3XrSpmQHkM7gKt3z7ELraW+jN6flF++N7o2cshrjzzkJZMyNcUIczeF8cRz5fadMBIlzGLFzGI8XB5xc/sGGzNWHVS5torLmCrjkcihtYBh2TCxpi75nu7RzgBuggxOBqu3bpubjrfZmMHVsiQRwBDw5AgMBAAECggEAeZvlRBqfJforzKZV/vMYWHRiJSZkBzgOnngJBfLTg88jSLDkRk1S59hU4sC32sxD7DURhm1pmwBRHRLSOYKOuMYGkT6Hzq9saknE6VU6GTW3Lwhi3J6xg8F4HfYOYYUK3DzIxAPG+cHBavCxfogTjtf4GGjGakNSenvqJyH0CWSLhJy4FKH21dmKCmkETfROfh2T+oLGna0LpyH0HSY2esIVqnMPe113sxBR8ixZtbxDfWVaSUW8hPuDY3CRvyJvOtVudVPEnq2zo5adwd76CqK1p8KacVkFYKfUZu2WLGAZ/vq4GhDsH2mFQFVFGtno3X2W3FlzLZZuL23XoHQY0QKBgQDJiHL45YKjKAzTjE5+jU0LnAioAYDX3zb2+4JME32SH5HZjNkyVhS+vhzGobyxaDgOi9uybfU29zPy3FEDltbOXCj0DdolJ66iNX9zKGiAJ3DppXxB1F8NrISzYH8wRJkGbj/f78IbHESU8W/+hwPs4ifcCG48bGtH/NRghtZ6nQKBgQCuYLurPynF5G9MWbrsb9Dpd9lxSrE4/4RJ3WzkGlAqHHEZD4aFYjr4YdbxHRbEjyLDOn5Pw97cv/bGUJtDYCVUReYSsFBY6ZeAfLjiIcj60/notdLEUeEL6jkgsaodV24qRq1lp6HgWWYuJ+MUIcgmpbJm+YgfgNjQVexjik5XTQKBgFor1h1tO11bDxocPga7nExkfS8GtSphckthzzPQyK93tp9bONcf0tphX6i0NyQ9mE9R3nPgZQY55yFz2D+93tCoRZ6lEATt02I6g5L38oHco9W2uHkP15Pbq+hCdcYX+0v4SlYdBYFxzXHC8TQ2Hth/fAwkLYaVkA6+nhuuWQq1AoGBAImAobuRXQE89j6AaxNRwKy44u+g9Ep7wfb08Y6U6S2rq8sVIQBAyztc+z65r2Bjdgf4Me34B96OSKtsmGHyiL0BOUD3YwQZfmvE7YzzEyYmzOOhrIc+AXWBxnZvYjMs5CPWA5krLExacmZm8XrIgKlo81qb3ajp0w3FE1gSkV/pAoGAJLLMuHKX+0Bc8YH7W39n4F9bP9zYyins0Xn0rae7GB/a1oKJc5LHVHMaclEKR09dtR4CWxwug6O5yYULn9z2pBn/pmfZFwFN/NAbDRidnbUoVrypW7GIRx4znO9iMF0CLAnJDHlHIQYDdbmyrcq4BDcOCUNnzaD7BGIEHViXPyY=";

    /**
     * 参数返回格式
     */
    public static final  String FORMAT="json";

    /**
     * 编码集
     */
    public static final  String CHARSET="UTF-8";

    /**
     * 支付宝公钥
     */
    public static final  String ALIPAY_PUBLIC_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1/mf+2ndsEy7l5ZvKsvYk4HQ+FrfpDZHw/nsP7u0rUsVew6z8NT+Dw9mQ+mejc90W1kzUGcg0+AQQ3dWAEu0a2WAQtRAQO0jTgASdfp5/ZkD81V2h/blw6/dqQoI5H/1Jde365ndEusYEfXjEgHFqNJjcTSKJ0cF+Ael1ICoCXMqEp+ybjW5MqnO1wJxUI8T/4L4Pdk6NzngPnVcoeaEo7TkQtKE41MLaYa0DzdeZauo8fX05IWb/IVzkfycGCN75cdLhP76IRBJdH04rIGUsvupj7ao36HlxpIpLWwj/11YcF9psi9Wgmx6lpgMRHm/a8x0XkjF2B5xbjIQrW/9DwIDAQAB";

    /**
     * 商户生成签名字符串所使用的签名算法类型
     */
    public static final  String SIGN_TYPE="RSA2";

    /**
     * 回调地址
     */
    public static final String LOGIN_BACK="http://liaoliaoba.com/api/user/zhifubao/back";
}
