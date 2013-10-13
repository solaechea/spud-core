package spud.core

class SecurityFilters {

	def grailsApplication

	def filters = {
		all(controller:'*', action:'*') {
			before = {
				def context = grailsApplication.mainContext
				def spudSecurityService = context[grailsApplication.config.spud.securityService ? grailsApplication.config.spud.securityService : 'abstractSpudSecurityService']
				def controllerClass = grailsApplication.controllerClasses.find {it.logicalPropertyName == controllerName}
				def action
				if(controllerClass) {
					action = applicationContext.getBean(controllerClass.fullName).class.declaredFields.find { field -> field.name == actionName }
				}
				def annotation = action?.getAnnotation(SpudSecure)

				if(!annotation && controllerClass) {
					annotation = controllerClass.clazz.getAnnotation(SpudSecure)
				}

				if(!annotation) {
					return true  //No Security Restrictions
				}

				if(!spudSecurityService.isAuthorized(annotation, request, params)) {
					spudSecurityService.storeLocation(request)
					redirect(spudSecurityService.loginUrl)
					return false
				}
				return true
			}
		}
	}
}