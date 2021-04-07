const dgram = require('dgram');
const server = dgram.createSocket('udp4');

server.on('error', (err) => {
  console.log(`server error:\n${err.stack}`);
  server.close();
});

server.on('message', (msg, rinfo) => {
  console.log(`server got: ${msg} from ${rinfo.address}:${rinfo.port}`);
  var Ansible = require('node-ansible');
  var data = [];
  data = JSON.parse(msg);
  if(data.action == 'add_rule') {
 	 console.log(data.action);
	 console.log(data.ip_in);
	 console.log(data.ip_out);
	 var playbook = new Ansible.Playbook().playbook('/home/ansible/add_rule').inventory('/home/ansbile/hosts').variables({ ip_in: data.ip_in, ip_out: data.ip_out });
	 playbook.inventory('/home/ansbile/hosts');
	 playbook.on('stdout', function(data) {
	 	console.log(data.toString());
  	 });
	 var promise = playbook.exec();
	 promise.then(function(successResult) {
	 	console.log(successResult.code);
  	 	console.log(successResult.output);
  	 }, function(error) {
  	 	console.error(error);
	 })
  }
  if(data.action == 'delete_rule') {
         console.log(data.action);
         console.log(data.ip_in);
         console.log(data.ip_out);
         var playbook = new Ansible.Playbook().playbook('/home/ansible/delete_rule').inventory('/home/ansbile/hosts').variables({ ip_in: data.ip_in, ip_out: data.ip_out });
         playbook.inventory('/home/ansbile/hosts');
         playbook.on('stdout', function(data) {
                console.log(data.toString());
         });
         var promise = playbook.exec();
         promise.then(function(successResult) {
                console.log(successResult.code);
                console.log(successResult.output);
         }, function(error) {
                console.error(error);
         })
  }
  if(data.action == 'delete_all_rule') {
         console.log(data.action);
         var playbook = new Ansible.Playbook().playbook('/home/ansible/delete_all_rule').inventory('/home/ansbile/hosts');
         playbook.inventory('/home/ansbile/hosts');
         playbook.on('stdout', function(data) {
                console.log(data.toString());
         });
         var promise = playbook.exec();
         promise.then(function(successResult) {
                console.log(successResult.code);
                console.log(successResult.output);
         }, function(error) {
                console.error(error);
         })
  }
  if(data.action == 'get_ip') {
         console.log(data.action);
         console.log(data.ip_in);
         var playbook = new Ansible.Playbook().playbook('/home/ansible/get_ip').inventory('/home/ansbile/hosts').variables({ ip_in: data.ip_in });
         playbook.inventory('/home/ansbile/hosts');
         playbook.on('stdout', function(data) {
                console.log(data.toString());
         });
         var promise = playbook.exec();
         promise.then(function(successResult) {
                console.log(successResult.code);
                console.log(successResult.output);
         }, function(error) {
                console.error(error);
         })
  }
  if(data.action == 'modify_ip') {
         console.log(data.action);
         console.log(data.interface);
         console.log(data.ip_in_new);
         var playbook = new Ansible.Playbook().playbook('/home/ansible/modify_ip').inventory('/home/ansbile/hosts').variables({ interface: data.interface, ip_in_new: data.ip_in_new });
         playbook.inventory('/home/ansbile/hosts');
         playbook.on('stdout', function(data) {
                console.log(data.toString());
         });
         var promise = playbook.exec();
         promise.then(function(successResult) {
                console.log(successResult.code);
                console.log(successResult.output);
         }, function(error) {
                console.error(error);
         })
  }
  if(data.action == 'modify_rule') {
         console.log(data.action);
         console.log(data.ip_in_old);
         console.log(data.ip_out_old);
         console.log(data.ip_in_new);
         console.log(data.ip_out_new);

         var playbook = new Ansible.Playbook().playbook('/home/ansible/modify_rule').inventory('/home/ansbile/hosts').variables({ ip_in_old: data.ip_in_old, ip_out_old: data.ip_out_old, ip_in_new: data.ip_in_new, ip_out_new: data.ip_out_new });
         playbook.inventory('/home/ansbile/hosts');
         playbook.on('stdout', function(data) {
                console.log(data.toString());
         });
         var promise = playbook.exec();
         promise.then(function(successResult) {
                console.log(successResult.code);
                console.log(successResult.output);
         }, function(error) {
                console.error(error);
         })
  }
  if(data.action == 'ping_show_iptables') {
         console.log(data.action);
         var playbook = new Ansible.Playbook().playbook('/home/ansible/ping_show_iptables').inventory('/home/ansbile/hosts');
         playbook.inventory('/home/ansbile/hosts');
         playbook.on('stdout', function(data) {
                console.log(data.toString());
         });
         var promise = playbook.exec();
         promise.then(function(successResult) {
                console.log(successResult.code);
                console.log(successResult.output);
         }, function(error) {
                console.error(error);
         })
  }
});

server.on('listening', () => {
  const address = server.address();
  console.log(`server listening ${address.address}:${address.port}`);
});

server.bind(3345);
