package fr.mbds.tp

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class HomeController {

    HomeService homeService
    UserProfileService userProfileService


    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        User utilisateurCourant = userProfileService.getCurrentUser()
        respond homeService.list(params), model:[homeCount: homeService.count(), username:utilisateurCourant.username]
    }

    def show(Long id) {
        respond homeService.get(id)
    }

    def create() {
        respond new Home(params)
    }

    def save(Home home) {
        if (home == null) {
            notFound()
            return
        }

        try {
            homeService.save(home)
        } catch (ValidationException e) {
            respond home.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'home.label', default: 'Home'), home.id])
                redirect home
            }
            '*' { respond home, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond homeService.get(id)
    }

    def update(Home home) {
        if (home == null) {
            notFound()
            return
        }

        try {
            homeService.save(home)
        } catch (ValidationException e) {
            respond home.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'home.label', default: 'Home'), home.id])
                redirect home
            }
            '*'{ respond home, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        homeService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'home.label', default: 'Home'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'home.label', default: 'Home'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

}
