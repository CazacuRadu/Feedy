package com.joinerni.feedy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.joinerni.feedy.domain.Form;
import com.joinerni.feedy.service.FormService;
import com.joinerni.feedy.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Form.
 */
@RestController
@RequestMapping("/api")
public class FormResource {

    private final Logger log = LoggerFactory.getLogger(FormResource.class);
        
    @Inject
    private FormService formService;
    
    /**
     * POST  /forms -> Create a new form.
     */
    @RequestMapping(value = "/forms",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Form> createForm(@RequestBody Form form) throws URISyntaxException {
        log.debug("REST request to save Form : {}", form);
        if (form.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("form", "idexists", "A new form cannot already have an ID")).body(null);
        }
        Form result = formService.save(form);
        return ResponseEntity.created(new URI("/api/forms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("form", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /forms -> Updates an existing form.
     */
    @RequestMapping(value = "/forms",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Form> updateForm(@RequestBody Form form) throws URISyntaxException {
        log.debug("REST request to update Form : {}", form);
        if (form.getId() == null) {
            return createForm(form);
        }
        Form result = formService.save(form);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("form", form.getId().toString()))
            .body(result);
    }

    /**
     * GET  /forms -> get all the forms.
     */
    @RequestMapping(value = "/forms",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Form> getAllForms() {
        log.debug("REST request to get all Forms");
        return formService.findAll();
            }

    /**
     * GET  /forms/:id -> get the "id" form.
     */
    @RequestMapping(value = "/forms/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Form> getForm(@PathVariable Long id) {
        log.debug("REST request to get Form : {}", id);
        Form form = formService.findOne(id);
        return Optional.ofNullable(form)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /forms/:id -> delete the "id" form.
     */
    @RequestMapping(value = "/forms/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteForm(@PathVariable Long id) {
        log.debug("REST request to delete Form : {}", id);
        formService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("form", id.toString())).build();
    }
}
