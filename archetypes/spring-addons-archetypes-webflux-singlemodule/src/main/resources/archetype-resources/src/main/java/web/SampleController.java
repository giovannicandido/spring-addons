package ${package}.web;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ${package}.domain.SampleEntity;
import ${package}.r2dbc.SampleEntityRepository;
import ${package}.web.dtos.SampleEditDto;
import ${package}.web.dtos.SampleResponseDto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/${api-path}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
@RequiredArgsConstructor
public class SampleController {
	private final SampleEntityRepository sampleRepo;
	private final SampleMapper sampleMapper;

	@GetMapping
	public Flux<SampleResponseDto> retrieveAll() {
		return sampleRepo.findAll().map(sampleMapper::toDto);
	}

	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public Mono<ResponseEntity<?>> create(@Valid @RequestBody SampleEditDto dto) throws URISyntaxException {
		final var tmp = new SampleEntity();
		sampleMapper.update(tmp, dto);

		return sampleRepo.save(tmp).map(sample -> {
			try {
				return ResponseEntity.created(new URI(String.format("/%d", sample.getId()))).build();
			} catch (final URISyntaxException e) {
				throw new RuntimeException(e);
			}
		});
	}

	@GetMapping("/{id}")
	public Mono<SampleResponseDto> retrieveById(@PathVariable(name = "id") long id) {
		return sampleRepo.findById(id).map(sampleMapper::toDto);
	}

	@PutMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public Mono<ResponseEntity<?>> update(
			@PathVariable(name = "id") @Parameter(name = "id", in = ParameterIn.PATH, required = true, schema = @Schema(type = "long")) SampleEntity sample,
			@Valid @RequestBody SampleEditDto dto) {
		sampleMapper.update(sample, dto);
		return sampleRepo.save(sample).map(s -> ResponseEntity.accepted().build());
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public Mono<ResponseEntity<?>> delete(
			@PathVariable(name = "id") @Parameter(name = "id", in = ParameterIn.PATH, required = true, schema = @Schema(type = "long")) SampleEntity sample) {

		return sampleRepo.delete(sample).map(s -> ResponseEntity.accepted().build());
	}
}
