import boto3
import json


def lambda_handler(event, context):
    # Obtén el ID de la conversación desde los parámetros de la ruta
    body = json.loads(event["application/json"])
    conversation_id = body['conversation_id']

    # Crea una instancia del cliente de DynamoDB
    dynamodb = boto3.client('dynamodb')

    # Realiza una consulta a la tabla para obtener todos los mensajes de la conversación
    response = dynamodb.query(
        TableName='messages',
        IndexName='conversation_id-index',
        KeyConditionExpression='conversation_id = :id',
        ExpressionAttributeValues={
            ':id': {'S': conversation_id}
        }
    )

    # Obtiene los mensajes de la respuesta
    mensajes = response['Items']

    # Convierte los mensajes a un formato legible
    mensajes_formateados = []
    for mensaje in mensajes:
        mensaje_formateado = {
            'message_id': mensaje['message_id']['S'],
            'content': mensaje['content']['S'],
            'conversation_id': mensaje['conversation_id']['S'],
            'conversation_label': mensaje['conversation_label']['S'],
            'message_type': mensaje['message_type']['S'],
            'sender_id': mensaje['sender_id']['S'],
            'status': int(mensaje['status']['N']),
            'timeSend': int(mensaje['timeSend']['N']),
            'pathS3': mensaje['pathS3']['S']
        }
        mensajes_formateados.append(mensaje_formateado)

    # Retorna los mensajes en el cuerpo de la respuesta
    response = {
        'statusCode': 200,
        'body': json.dumps(mensajes_formateados)
    }
    return response

