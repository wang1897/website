package com.aethercoder.foundation.security.encryption;

/**
 * Created by hepengfei on 25/12/2017.
 */
//@Component
//@Scope("prototype")
public class HttpEncodeMessageConverter {
// extends AbstractHttpMessageConverter<Object> {

    /*public static final Charset DEFAULT_CHARSET = Charset.forName(WalletConstants.CHARACTER_ENCODE);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

//    @Inject
//    private ObjectMapper objectMapper;

    public HttpEncodeMessageConverter() {
        super(MediaType.APPLICATION_JSON_UTF8, new MediaType("application", "*+json", DEFAULT_CHARSET));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected Object readInternal(Class<? extends Object> clazz,
                                  HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        try {
            InputStream is = inputMessage.getBody();
            String bodyStr = IOUtils.toString(is, WalletConstants.CHARACTER_ENCODE);
            String result = AESUtil.decrypt(bodyStr, WalletConstants.AES_KEY);
            return BeanUtils.jsonToObject(result, clazz);
        } catch (Exception e) {
            throw new HttpMessageNotReadableException(e.getMessage());
        }

    }

    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try {
            ServletServerHttpResponse response = (ServletServerHttpResponse)outputMessage;
            if (!(o instanceof InputStreamResource)) {
                HttpHeaders httpHeaders = outputMessage.getHeaders();
                List<String> headersList = httpHeaders.get(WalletConstants.HEADER_NO_ENCRYPTION);
                if (headersList != null && !headersList.isEmpty() && WalletConstants.HEADER_NO_ENCRYPTION_VALUE.equals(headersList.get(0))) {
                    if (o instanceof String) {
                        outputMessage.getBody().write(o.toString().getBytes());
                        return;
                    } else {
                        outputMessage.getBody().write(BeanUtils.objectToJson(o).getBytes());
                        return;
                    }
                }
                String json = BeanUtils.objectToJson(o);
                String encrypted = AESUtil.encrypt(json, WalletConstants.AES_KEY);
                outputMessage.getBody().write(encrypted.getBytes(WalletConstants.CHARACTER_ENCODE));
            } else {
                InputStreamResource isr = (InputStreamResource)o;
                InputStream is = isr.getInputStream();
                OutputStream os = outputMessage.getBody();
                int temp = 0;
                while((temp = is.read()) != -1){
                    os.write(temp);
                }
            }

        } catch (Exception e) {
            throw new HttpMessageNotReadableException(e.getMessage());
        }
    }*/
}