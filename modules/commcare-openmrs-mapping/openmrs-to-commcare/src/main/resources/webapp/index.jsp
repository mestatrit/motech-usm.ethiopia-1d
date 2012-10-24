<!doctype html>

<html>
	<head>
	
	</head>
	<body>
		<h1>Commcare Mapping</h1>
		<form method="POST">
			<p>
			    Event to raise:
			    <select name="eventToRaise">
			     <option value="obsUpdate">Observation Update</option>
			     <option value="encounterCreate">Encounter Create</option>
			    </select>
			</p>
			<p>
				Enter uuid: <input type="text" size="32" name="objectUuid" />
				<br />
				<input type="submit" value="Send Event" />
			</p>
		</form>
	</body>
</html>