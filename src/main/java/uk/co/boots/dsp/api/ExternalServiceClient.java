package uk.co.boots.dsp.api;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.boots.gs1.api.GSOneBarcodeRequest;

import reactor.core.publisher.Mono;
import com.boots.gs1.data.GSOneBarcode;

@Service
public class ExternalServiceClient {

	private final WebClient webClient;
	
	public ExternalServiceClient (WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl("http://localhost:9091").build();
	}

	public class CustomClientException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		private final HttpStatus status;
	    private final String responseBody;

	    public CustomClientException(String message, WebClientResponseException cause) {
	        super(message, cause);
	        this.status = cause.getStatusCode();
	        this.responseBody = cause.getResponseBodyAsString();
	    }

	    public HttpStatus getStatus() {
	        return status;
	    }

	    public String getResponseBody() {
	        return responseBody;
	    }
	}

	public Mono<GSOneBarcode> getGSOne(String barcode) {
		GSOneBarcodeRequest req = new GSOneBarcodeRequest(false, barcode);
		return webClient.post()
		        .uri("/gs1utils/barcodeFrom")
		        .bodyValue(req)
		        .retrieve()
		        .onStatus(HttpStatus::is4xxClientError, response ->
		            response.createException().map(ex -> {
		                System.err.println("Status: " + ex.getStatusCode());
		                System.err.println("Body: " + ex.getResponseBodyAsString());
		                System.err.println("Request: " + ex.getRequest());
		                return new CustomClientException("Client error", ex);
		            })
		        )
		        .bodyToMono(GSOneBarcode.class);
	}
}
