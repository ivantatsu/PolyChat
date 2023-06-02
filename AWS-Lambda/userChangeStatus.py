import boto3

dynamodb = boto3.resource('dynamodb')
table_name = 'users'


def lambda_handler(event, context):
    # Obtén el user_id y el nuevo status de los parámetros del evento
    user_id = event['user_id']
    new_status = event['new_status']

    # Carga el usuario actual desde DynamoDB
    table = dynamodb.Table(table_name)
    response = table.get_item(Key={'user_id': user_id})

    if 'Item' not in response:
        return {
            'statusCode': 404,
            'body': 'Usuario no encontrado'
        }

    user = response['Item']

    # Actualiza el status del usuario
    user['status'] = new_status

    # Guarda la versión actualizada del usuario en DynamoDB
    response = table.put_item(Item=user)

    return {
        'statusCode': 200,
        'body': 'Status actualizado correctamente'
    }
