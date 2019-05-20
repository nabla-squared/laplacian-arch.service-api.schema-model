package laplacian_arch.service_api_arch.record
import com.github.jknack.handlebars.Context
import laplacian.gradle.task.generate.model.Project
import laplacian_arch.service_api_arch.model.Service
import laplacian_arch.service_api_arch.model.ServiceList
import laplacian_arch.service_api_arch.model.ResourceEntry
import laplacian_arch.service_api_arch.model.RestResource
import laplacian_arch.datasource.model.Datasource
import laplacian_arch.datasource.record.DatasourceRecord
import laplacian.metamodel.model.Entity
import laplacian.metamodel.record.EntityRecord
import laplacian_arch.service_api_arch.model.GraphqlQuery
import laplacian.util.*
/**
 * service
 */
data class ServiceRecord (
    private val __record: Record,
    private val _context: Context,
    private val _record: Record = __record.normalizeCamelcase()
): Service, Record by _record {
    /**
     * The laplacian module project definition.
     */
    private val project: Project
        get() = _context.get("project") as Project


    /**
     * The name of this service.
     */
    override val name: String
        get() = getOrThrow("name")

    /**
     * The identifier of this service.
     */
    override val identifier: String
        get() = getOrThrow("identifier") {
            name.lowerUnderscorize()
        }

    /**
     * The version of this service.
     */
    override val version: String
        get() = getOrThrow("version")

    /**
     * The api_version of this service.
     */
    override val apiVersion: String
        get() = getOrThrow("apiVersion") {
            "v1"
        }

    /**
     * The endpoint of this service.
     */
    override val endpoint: String
        get() = getOrThrow("endpoint") {
            "${protocol}://${host}:${port}${path}/${apiVersion}"
        }

    /**
     * The host of this service.
     */
    override val host: String
        get() = getOrThrow("host") {
            "identifier.lowerHyphenize()"
        }

    /**
     * The port of this service.
     */
    override val port: Int
        get() = getOrThrow("port") {
            8080
        }

    /**
     * The protocol of this service.
     */
    override val protocol: String
        get() = getOrThrow("protocol") {
            "http"
        }

    /**
     * The path of this service.
     */
    override val path: String
        get() = getOrThrow("path") {
            "/${identifier.lowerHyphenize()}"
        }

    /**
     * The namespace of this service.
     */
    override val namespace: String
        get() = getOrThrow("namespace") {
            "${_context.get("project.namespace")}.service.$identifier"
        }

    /**
     * The description of this service.
     */
    override val description: String
        get() = getOrThrow("description") {
            name
        }

    /**
     * The datasource_name of this service.
     */
    override val datasourceName: String
        get() = getOrThrow("datasourceName")

    /**
     * resource_entries
     */
    override val resourceEntries: List<ResourceEntry>
        = ResourceEntryRecord.from(_record.getList("resource_entries", emptyList()), _context, this)

    /**
     * datasource
     */
    override val datasource: Datasource
        get() = DatasourceRecord.from(_context).find {
            it.name == datasourceName
        } ?: throw IllegalStateException(
            "There is no datasource which meets the following condition(s): "
            + "Service.datasource_name == datasource.name (=$datasourceName) "
            + "Possible values are: " + DatasourceRecord.from(_context).map {
              "(${ it.name })"
            }.joinToString()
        )

    /**
     * graphql_queries
     */
    override val graphqlQueries: List<GraphqlQuery>
        = GraphqlQueryRecord.from(_record.getList("graphql_queries", emptyList()), _context, this)

    companion object {
        /**
         * creates record list from list of map
         */
        fun from(_context: Context): ServiceList {
            return _context.get("services") as ServiceList
        }
    }
}