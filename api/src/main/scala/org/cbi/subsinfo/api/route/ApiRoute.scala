package org.cbi.subsinfo.api.route

trait ApiRoute extends LegacyStisRoute with BlackListRoute with SubscribersRoute {

  def routes = legacyStisRoute ~ blackListRoute ~ subscriberRoute
}