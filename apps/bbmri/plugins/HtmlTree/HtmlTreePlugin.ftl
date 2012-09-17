<#macro plugins_HtmlTree_HtmlTreePlugin screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
<!-- this shows a title and border -->
	<div class="formscreen">
		<div class="form_header" id="${screen.getName()}">
		${screen.label}
		</div>
		
		<#--optional: mechanism to show messages-->
		<#list screen.getMessages() as message>
			<#if message.success>
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
		<div class="screenbody">
			<div class="screenpadding">	
				<ul class="mktree" id="tree1">
								<li>cache/
									<ul>
										<li>delete_me.txt</li>
									</ul>
								</li>
								<li class="liOpen">calendars/
									<ul>
										<li class="liOpen">_template/
											<ul>
												<li>config.txt</li>
												<li>config.txt.lock</li>
												<li>events.id</li>
												<li>events.lock</li>
												<li>events.properties</li>
												<li>events.txt</li>
												<li>schedule.id</li>
												<li>schedule.lock</li>
												<li>schedule.properties</li>
												<li>schedule.txt</li>
											</ul>
										</li>
										<li>default/
											<ul>
												<li>config.txt</li>
												<li>config.txt.lock</li>
												<li>events.id</li>
												<li>events.lock</li>
												<li>events.properties</li>
												<li>events.txt</li>
												<li>schedule.id</li>
												<li>schedule.lock</li>
												<li>schedule.properties</li>
												<li>schedule.txt</li>
											</ul>
										</li>
									</ul>
								</li>
								<li>calendars.id</li>
								<li>calendars.lock</li>
								<li>calendars.properties</li>
								<li>calendars.txt</li>
								<li>command_list.txt</li>
								<li>config.txt</li>
								<li>config.txt.lock</li>
								<li>lib/
									<ul>
										<li>CGISession.inc</li>
										<li>ConfigFile.pm</li>
										<li>DBFile.pm</li>
										<li>DBFileUtil.inc</li>
										<li>Date.inc</li>
										<li>Event.inc</li>
										<li>HTML.pm</li>
										<li>SimpleDateFormat.pm</li>
										<li>TimeLocal.inc</li>
										<li>User.pm</li>
										<li>asp.inc</li>
										<li>calendars.inc</li>
										<li>flock.inc</li>
									</ul>
								</li>
								<li>permissions.id</li>
								<li>permissions.lock</li>
								<li>permissions.properties</li>
								<li>permissions.txt</li>
								<li>permissions_list.txt</li>
								<li>plugins/
									<ul>
										<li>TestPlugin/
											<ul>
												<li>before_commands.pl</li>
												<li>command_list.txt</li>
												<li>plugin_test.html</li>
												<li>readme.txt</li>
											</ul>
										</li>
										<li>command_list.txt</li>
										<li>permissions_list.txt</li>
										<li>plugin_files.txt</li>
										<li>plugins.txt</li>
									</ul>
								</li>
								<li>session/
									<ul>
										<li>delete_me.txt</li>
									</ul>
								</li>
								<li>ssi.txt</li>
								<li>templates/
									<ul>
										<li>admin/
											<ul>
												<li>English/
													<ul>
														<li>AnchorPosition.js</li>
														<li>CalendarPopup.js</li>
														<li>ColorPicker.js</li>
														<li>GetDate.js</li>
														<li>PopupWindow.js</li>
														<li>TimeValidations.js</li>
														<li>_command_list.html</li>
														<li>_footer.html</li>
														<li>_header.html</li>
														<li>add_edit_calendar.html</li>
														<li>add_edit_event.html</li>
														<li>add_edit_field.html</li>
														<li>add_edit_user.html</li>
														<li>admin_interface.html</li>
														<li>approve.html</li>
														<li>calendar_settings.html</li>
														<li>change_password.html</li>
														<li>customize_event_fields.html</li>
														<li>customize_user_fields.html</li>
														<li>dates.js</li>
														<li>edit_delete_events.html</li>
														<li>edit_delete_users.html</li>
														<li>error.html</li>
														<li>feedback.html</li>
														<li>import_events.html</li>
														<li ID="login">login.html</li>
														<li>main.html</li>
														<li>manage_calendars.html</li>
														<li>messages.txt</li>
														<li>permissions.html</li>
														<li>permissions_error.html</li>
														<li>plugins.html</li>
														<li>schedule_event_non_recur.html</li>
														<li>schedule_event_recurring.html</li>
														<li>select_calendar.html</li>
														<li>selectbox.js</li>
														<li>setup1.html</li>
														<li>setup2.html</li>
														<li>setup3.html</li>
														<li>setup_complete.html</li>
														<li>styles.css</li>
														<li>tabnext.js</li>
														<li>template.html</li>
														<li>template_preferences.html</li>
														<li>util.js</li>
														<li>validations.js</li>
													</ul>
												</li>
											</ul>
										</li>
										<li>calendars/
											<ul>
												<li>default/
													<ul>
														<li>default.html</li>
														<li>error.html</li>
														<li>login.html</li>
														<li>preferences.pl</li>
														<li>preferences.txt</li>
														<li>ssi.html</li>
														<li>styles.css</li>
													</ul>
												</li>
												<li>oldstyle/
													<ul>
														<li>_view.html</li>
														<li>day.html</li>
														<li>default.html</li>
														<li>error.html</li>
														<li>event_detail.html</li>
														<li>login.html</li>
														<li>search.html</li>
														<li>ssi.htmlv
														<li>styles_scripts.html</li>
													</ul>
												</li>
												<li>simple/
													<ul>
														<li>default.html</li>
														<li>error.html</li>
														<li>login.html</li>
														<li>ssi.html</li>
													</ul>
												</li>
											</ul>
										</li>
									</ul>
								</li>
								
							</ul>
										
						</div>
					</div>

		</div>
	</div>
</form>

</#macro>
